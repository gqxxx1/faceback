package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.PageResult;
import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.FaceData;
import com.sdjzu.faceattendancesystem.entity.Personnel;
import com.sdjzu.faceattendancesystem.service.FaceDataService;
import com.sdjzu.faceattendancesystem.service.PersonnelService;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/face")
public class FaceDataController {

    private static final Logger log = LoggerFactory.getLogger(FaceDataController.class);

    @Autowired
    private FaceDataService faceDataService;
    @Autowired
    private PersonnelService personnelService;

    @Value("${face.storage.path:./face_images}")
    private String faceStoragePath;

    private static final int FACE_IMAGE_WIDTH = 200;
    private static final int FACE_IMAGE_HEIGHT = 200;
    private static final int MIN_FACE_SIZE = 80;
    private static final int MAX_FACE_SIZE = 400;

    private CascadeClassifier faceDetector;

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            log.info("✅ OpenCV 加载成功");
        } catch (UnsatisfiedLinkError e) {
            try {
                nu.pattern.OpenCV.loadLocally();
            } catch (Exception ex) {
                log.error("❌ OpenCV 加载失败", ex);
            }
        }
    }

    public FaceDataController() {
        try {
            var resource = new org.springframework.core.io.ClassPathResource("haarcascade/haarcascade_frontalface_default.xml");
            File tempFile = java.io.File.createTempFile("cascade", ".xml");
            Files.copy(resource.getInputStream(), tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            tempFile.deleteOnExit();
            faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());
            log.info("✅ 人脸检测模型加载成功");
        } catch (Exception e) {
            log.error("❌ 人脸模型加载失败", e);
            faceDetector = new CascadeClassifier();
        }
    }

    // ====================== 人脸采集（核心接口） ======================

    /**
     * 采集人脸 - 前端拍照后调用此接口
     * 后端负责：检测人脸、图像处理、特征提取、保存
     */
    @PostMapping("/capture")
    public Result<Map<String, Object>> captureFace(@RequestBody Map<String, Object> request) {
        Long personnelId = Long.valueOf(request.get("personnelId").toString());
        String base64 = (String) request.get("imageBase64");
        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }
        String angle = request.getOrDefault("captureAngle", "正面").toString();
        String location = request.getOrDefault("location", "办公室").toString();

        try {
            Personnel p = personnelService.getById(personnelId);
            if (p == null) return Result.error("人员不存在");

            byte[] bytes = Base64.getDecoder().decode(base64);
            Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
            if (img.empty()) return Result.error("图片无效");

            // 人脸检测与处理
            ProcessResult pr = detectAndProcessFace(img);
            if (!pr.isFaceDetected()) {
                img.release();
                return Result.error("未检测到人脸，请确保正对摄像头");
            }

            // 质量检查
            if (pr.getQualityScore() < 0.25) {
                img.release();
                pr.release();
                return Result.error("图像质量不佳，请确保光线充足");
            }

            // 特征提取
            String descriptor = extractFaceDescriptor(pr.getEqualizedFace());
            String path = saveFaceImage(pr.getEqualizedFace(), p.getEmployeeId());
            byte[] faceBytes = matToBytes(pr.getGrayFace());

            // 保存到数据库
            FaceData faceData = new FaceData();
            faceData.setPersonnelId(personnelId);
            faceData.setEmployeeId(p.getEmployeeId());
            faceData.setFaceImage(faceBytes);
            faceData.setFaceImagePath(path);
            faceData.setFaceDescriptor(descriptor);
            faceData.setImageWidth(FACE_IMAGE_WIDTH);
            faceData.setImageHeight(FACE_IMAGE_HEIGHT);
            faceData.setCaptureAngle(angle);
            faceData.setCaptureLocation(location);
            faceData.setImageQualityScore(pr.getQualityScore());
            faceData.setCaptureTime(LocalDateTime.now());
            faceData.setIsValid(1);
            faceData.setDeleted(0);

            int existingCount = faceDataService.getValidFaceCount(personnelId);
            faceData.setIsPrimary(existingCount == 0 ? 1 : 0);

            faceDataService.saveFaceData(faceData);
            updatePersonnelFaceStatus(personnelId);

            img.release();
            pr.release();

            Map<String, Object> res = new HashMap<>();
            res.put("id", faceData.getId());
            res.put("count", faceDataService.getValidFaceCount(personnelId));
            res.put("quality", pr.getQualityScore());
            return Result.success(res, "人脸采集成功");

        } catch (Exception e) {
            log.error("采集失败", e);
            return Result.error("采集失败：" + e.getMessage());
        }
    }

    // ====================== 一步完成：创建员工并录入人脸 ======================

    @PostMapping("/createWithFace")
    public Result<Map<String, Object>> createWithFace(@RequestBody Map<String, Object> request) {
        log.info("收到创建员工（含人脸）请求");

        Map<String, Object> result = new HashMap<>();
        Personnel personnel = null;
        boolean faceCaptured = false;

        try {
            // 1. 保存人员信息
            personnel = new Personnel();
            personnel.setEmployeeId((String) request.get("employeeId"));
            personnel.setName((String) request.get("name"));
            personnel.setGender((String) request.get("gender"));

            Object ageObj = request.get("age");
            if (ageObj != null) {
                if (ageObj instanceof Integer) {
                    personnel.setAge((Integer) ageObj);
                } else {
                    personnel.setAge(Integer.parseInt(ageObj.toString()));
                }
            }

            personnel.setDepartment((String) request.get("department"));
            personnel.setPosition((String) request.get("position"));
            personnel.setPhone((String) request.get("phone"));
            personnel.setIdCard((String) request.get("idCard"));
            personnel.setStatus(1);

            boolean saved = personnelService.save(personnel);
            if (!saved) return Result.error("人员保存失败");

            Long personnelId = personnel.getId();
            log.info("人员创建成功，ID: {}", personnelId);

            // 2. 处理人脸
            String imageBase64 = (String) request.get("imageBase64");
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    if (imageBase64.contains(",")) {
                        imageBase64 = imageBase64.split(",")[1];
                    }

                    byte[] bytes = Base64.getDecoder().decode(imageBase64);
                    Mat img = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);

                    if (!img.empty()) {
                        ProcessResult pr = detectAndProcessFace(img);
                        if (pr.isFaceDetected() && pr.getQualityScore() >= 0.25) {
                            String descriptor = extractFaceDescriptor(pr.getEqualizedFace());
                            String savePath = saveFaceImage(pr.getEqualizedFace(), personnel.getEmployeeId());
                            byte[] faceBytes = matToBytes(pr.getGrayFace());

                            FaceData faceData = new FaceData();
                            faceData.setPersonnelId(personnelId);
                            faceData.setEmployeeId(personnel.getEmployeeId());
                            faceData.setFaceImage(faceBytes);
                            faceData.setFaceImagePath(savePath);
                            faceData.setFaceDescriptor(descriptor);
                            faceData.setImageWidth(FACE_IMAGE_WIDTH);
                            faceData.setImageHeight(FACE_IMAGE_HEIGHT);
                            faceData.setCaptureAngle("正面");
                            faceData.setImageQualityScore(pr.getQualityScore());
                            faceData.setCaptureTime(LocalDateTime.now());
                            faceData.setIsValid(1);
                            faceData.setIsPrimary(1);
                            faceData.setDeleted(0);

                            faceDataService.saveFaceData(faceData);
                            updatePersonnelFaceStatus(personnelId);
                            faceCaptured = true;

                            img.release();
                            pr.release();
                        } else {
                            log.warn("未检测到人脸或质量不佳");
                        }
                    }
                } catch (Exception e) {
                    log.error("人脸处理异常", e);
                }
            }

            result.put("id", personnel.getId());
            result.put("employeeId", personnel.getEmployeeId());
            result.put("name", personnel.getName());
            result.put("faceCaptured", faceCaptured);

            String message = faceCaptured ? "员工创建成功，人脸录入完成" : "员工创建成功，但人脸录入失败";
            return Result.success(result, message);

        } catch (Exception e) {
            log.error("创建员工失败", e);
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    // ====================== 其他接口 ======================

    @GetMapping("/list/{personnelId}")
    public Result<List<FaceData>> list(@PathVariable Long personnelId) {
        List<FaceData> list = faceDataService.getByPersonnelId(personnelId);
        list.forEach(f -> f.setFaceImage(null));
        return Result.success(list);
    }

    @GetMapping("/image/{id}")
    public Result<byte[]> image(@PathVariable Long id) {
        FaceData f = faceDataService.getById(id);
        return f != null && f.getFaceImage() != null ? Result.success(f.getFaceImage()) : Result.error("无");
    }

    @DeleteMapping("/{id}")
    public Result<Void> del(@PathVariable Long id) {
        FaceData f = faceDataService.getById(id);
        if (f != null) {
            faceDataService.removeById(id);
            updatePersonnelFaceStatus(f.getPersonnelId());
        }
        return Result.success();
    }

    @PutMapping("/setPrimary/{id}")
    public Result<Void> primary(@PathVariable Long id) {
        FaceData f = faceDataService.getById(id);
        if (f != null) faceDataService.setPrimary(id, f.getPersonnelId());
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<FaceData>> page(FaceData query, int current, int size) {
        var page = faceDataService.pageList(query, current, size);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/status/{pid}")
    public Result<Map<String, Object>> status(@PathVariable Long pid) {
        int cnt = faceDataService.getValidFaceCount(pid);
        Map<String, Object> map = new HashMap<>();
        map.put("registered", cnt > 0);
        map.put("count", cnt);
        map.put("ready", cnt >= 1);
        return Result.success(map);
    }

    // ====================== 核心：人脸检测与预处理 ======================

    private ProcessResult detectAndProcessFace(Mat original) {
        ProcessResult r = new ProcessResult();
        r.setFaceDetected(false);

        Mat gray = new Mat();
        Imgproc.cvtColor(original, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);

        MatOfRect rects = new MatOfRect();
        faceDetector.detectMultiScale(gray, rects, 1.1, 5, 0,
                new Size(MIN_FACE_SIZE, MIN_FACE_SIZE),
                new Size(MAX_FACE_SIZE, MAX_FACE_SIZE));

        Rect[] faces = rects.toArray();
        if (faces == null || faces.length == 0) {
            gray.release();
            rects.release();
            return r;
        }

        // 取最大人脸
        Rect best = faces[0];
        for (Rect f : faces) {
            if (f.area() > best.area()) best = f;
        }

        // 外扩 30%
        int pad = (int) (best.width * 0.3);
        int x = Math.max(0, best.x - pad);
        int y = Math.max(0, best.y - pad);
        int w = Math.min(original.cols() - x, best.width + pad * 2);
        int h = Math.min(original.rows() - y, best.height + pad * 2);

        Mat roi = new Mat(gray, new Rect(x, y, w, h));
        Mat resize = new Mat();
        Imgproc.resize(roi, resize, new Size(FACE_IMAGE_WIDTH, FACE_IMAGE_HEIGHT));

        Mat eq = new Mat();
        Imgproc.equalizeHist(resize, eq);

        r.setGrayFace(resize);
        r.setEqualizedFace(eq);
        r.setFaceDetected(true);
        r.setQualityScore(calculateImageQuality(eq));

        gray.release();
        roi.release();
        rects.release();

        return r;
    }

    // ====================== 工具方法 ======================

    private String extractFaceDescriptor(Mat mat) {
        Mat hist = new Mat();
        Imgproc.calcHist(Collections.singletonList(mat), new MatOfInt(0), new Mat(),
                hist, new MatOfInt(32), new MatOfFloat(0, 256));
        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) sb.append(hist.get(i, 0)[0]).append(",");
        hist.release();
        return sb.toString();
    }

    private double calculateImageQuality(Mat m) {
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(m, mean, std);
        double contrast = std.get(0, 0)[0] / 128.0;
        return Math.min(1.0, Math.max(0.2, contrast));
    }

    private String saveFaceImage(Mat mat, String empId) throws IOException {
        Path dir = Paths.get(faceStoragePath, empId);
        Files.createDirectories(dir);
        String name = "face_" + System.currentTimeMillis() + ".png";
        String path = dir.resolve(name).toString();
        Imgcodecs.imwrite(path, mat);
        return path;
    }

    private byte[] matToBytes(Mat mat) {
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buf);
        byte[] bytes = buf.toArray();
        buf.release();
        return bytes;
    }

    private void updatePersonnelFaceStatus(Long id) {
        Personnel p = personnelService.getById(id);
        if (p != null) {
            int cnt = faceDataService.getValidFaceCount(id);
            p.setFaceDataCount(cnt);
            p.setFaceRegistered(cnt > 0 ? 1 : 0);
            personnelService.updateById(p);
        }
    }

    // ====================== 内部类 ======================

    private static class ProcessResult {
        private Mat grayFace;
        private Mat equalizedFace;
        private boolean faceDetected;
        private double qualityScore;

        public Mat getGrayFace() { return grayFace; }
        public void setGrayFace(Mat grayFace) { this.grayFace = grayFace; }
        public Mat getEqualizedFace() { return equalizedFace; }
        public void setEqualizedFace(Mat equalizedFace) { this.equalizedFace = equalizedFace; }
        public boolean isFaceDetected() { return faceDetected; }
        public void setFaceDetected(boolean faceDetected) { this.faceDetected = faceDetected; }
        public double getQualityScore() { return qualityScore; }
        public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }

        public void release() {
            if (grayFace != null) grayFace.release();
            if (equalizedFace != null) equalizedFace.release();
        }
    }
}
