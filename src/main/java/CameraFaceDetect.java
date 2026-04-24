import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * 纯 Java + OpenCV 实时摄像头人脸检测（画框标记）
 */
public class CameraFaceDetect {

    // 加载 OpenCV 库（自动从 Maven 依赖加载）
    static {
        try {
            nu.pattern.OpenCV.loadLocally();
            System.out.println("✅ OpenCV 加载成功");
        } catch (Exception e) {
            System.err.println("❌ OpenCV 加载失败");
            e.printStackTrace();
        }
    }

    private static final String XML_PATH = "haarcascade/haarcascade_frontalface_default.xml";
    private CascadeClassifier faceDetector;
    private VideoCapture capture;
    private Mat frame;
    private JLabel screenLabel;

    // 初始化检测器
    public boolean initDetector() {
        faceDetector = new CascadeClassifier();
        try {
            // 从 classpath 加载模型
            var resource = new org.springframework.core.io.ClassPathResource(XML_PATH);
            var tempFile = java.io.File.createTempFile("cascade", ".xml");
            java.nio.file.Files.copy(resource.getInputStream(), tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            tempFile.deleteOnExit();
            faceDetector.load(tempFile.getAbsolutePath());
            System.out.println("✅ 人脸模型加载成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 打开摄像头
    public boolean openCamera() {
        capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.err.println("❌ 摄像头打开失败");
            return false;
        }
        // 设置分辨率
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        frame = new Mat();
        System.out.println("✅ 摄像头已打开，按 ESC 退出");
        return true;
    }

    // 主窗口
    public void createWindow() {
        JFrame frame = new JFrame("摄像头人脸检测");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screenLabel = new JLabel();
        frame.add(screenLabel);
        frame.setSize(650, 520);
        frame.setVisible(true);
    }

    // 开始实时检测
    public void startDetect() {
        while (true) {
            capture.read(frame);
            if (frame.empty()) break;

            // 转灰度 + 直方图均衡（提高识别率）
            Mat gray = new Mat();
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gray, gray);

            // 人脸检测
            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(gray, faces, 1.1, 5, 0,
                    new org.opencv.core.Size(80, 80),
                    new org.opencv.core.Size(400, 400));

            // 给每一张人脸画蓝色框
            for (Rect face : faces.toArray()) {
                Imgproc.rectangle(
                        frame,
                        new Point(face.x, face.y),
                        new Point(face.x + face.width, face.y + face.height),
                        new Scalar(255, 0, 0), // 蓝色 BGR
                        2 // 线条粗细
                );
            }

            // 显示画面
            screenLabel.setIcon(new ImageIcon(matToImage(frame)));

            gray.release();
            faces.release();

            // 按 ESC 退出
            if (Thread.interrupted() || isEscPressed()) break;
        }
        capture.release();
        frame.release();
    }

    // Mat 转 Swing 可显示图片
    public BufferedImage matToImage(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int channels = mat.channels();
        byte[] pixels = new byte[width * height * channels];
        mat.get(0, 0, pixels);

        BufferedImage image;
        if (channels == 3) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        byte[] imgPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, imgPixels, 0, pixels.length);
        return image;
    }

    // 检测 ESC
    private boolean isEscPressed() {
        return java.awt.event.KeyEvent.VK_ESCAPE == 0;
    }

    public static void main(String[] args) {
        CameraFaceDetect app = new CameraFaceDetect();
        if (!app.initDetector()) return;
        if (!app.openCamera()) return;
        app.createWindow();
        app.startDetect();
    }
}