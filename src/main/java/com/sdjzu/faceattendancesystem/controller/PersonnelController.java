package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.PageResult;
import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.FaceData;
import com.sdjzu.faceattendancesystem.entity.Personnel;
import com.sdjzu.faceattendancesystem.service.FaceDataService;
import com.sdjzu.faceattendancesystem.service.PersonnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员控制器
 */
@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private static final Logger log = LoggerFactory.getLogger(PersonnelController.class);

    @Autowired
    private PersonnelService personnelService;

    @Autowired
    private FaceDataService faceDataService;

    @GetMapping("/list")
    public Result<PageResult<Personnel>> list(Personnel query,
                                              @RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size) {
        var page = personnelService.pageList(query, current, size);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Personnel> getById(@PathVariable Long id) {
        return Result.success(personnelService.getById(id));
    }

    @GetMapping("/search")
    public Result<List<Personnel>> search(@RequestParam String name) {
        return Result.success(personnelService.searchByName(name));
    }

    @GetMapping("/employee/{employeeId}")
    public Result<Personnel> getByEmployeeId(@PathVariable String employeeId) {
        return Result.success(personnelService.getByEmployeeId(employeeId));
    }

    @PostMapping
    public Result<Map<String, Object>> create(@RequestBody Personnel personnel) {
        log.info("新增人员: {}", personnel);
        personnelService.save(personnel);

        // 返回完整的人员信息，包括人脸状态
        Map<String, Object> result = new HashMap<>();
        result.put("id", personnel.getId());
        result.put("employeeId", personnel.getEmployeeId());
        result.put("name", personnel.getName());
        result.put("faceRegistered", 0);
        result.put("faceDataCount", 0);
        result.put("message", "人员创建成功，请录入人脸信息");

        return Result.success(result, "创建成功");
    }

    /**
     * 创建人员并录入人脸信息（一步完成）
     */
    @PostMapping("/withFace")
    public Result<Map<String, Object>> createWithFace(@RequestBody Map<String, Object> request) {
        log.info("新增人员（含人脸）: {}", request);

        try {
            // 1. 解析人员信息
            Personnel personnel = new Personnel();
            personnel.setEmployeeId((String) request.get("employeeId"));
            personnel.setName((String) request.get("name"));
            personnel.setGender((String) request.get("gender"));
            if (request.get("age") != null) {
                personnel.setAge(Integer.parseInt(request.get("age").toString()));
            }
            personnel.setDepartment((String) request.get("department"));
            personnel.setPosition((String) request.get("position"));
            personnel.setPhone((String) request.get("phone"));
            personnel.setIdCard((String) request.get("idCard"));
            personnel.setStatus(1);

            // 2. 保存人员
            personnelService.save(personnel);

            // 3. 返回结果（前端将使用单独的API录入人脸）
            Map<String, Object> result = new HashMap<>();
            result.put("id", personnel.getId());
            result.put("employeeId", personnel.getEmployeeId());
            result.put("name", personnel.getName());
            result.put("faceRegistered", 0);
            result.put("faceDataCount", 0);
            result.put("needFaceCapture", true);

            return Result.success(result, "人员创建成功，请录入人脸信息");

        } catch (Exception e) {
            log.error("创建人员失败: ", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取人员详情（包含人脸状态）
     */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        Personnel personnel = personnelService.getById(id);
        if (personnel == null) {
            return Result.error("人员不存在");
        }

        // 获取人脸数据
        List<FaceData> faceDataList = faceDataService.getByPersonnelId(id);

        Map<String, Object> result = new HashMap<>();
        result.put("id", personnel.getId());
        result.put("employeeId", personnel.getEmployeeId());
        result.put("name", personnel.getName());
        result.put("gender", personnel.getGender());
        result.put("age", personnel.getAge());
        result.put("department", personnel.getDepartment());
        result.put("position", personnel.getPosition());
        result.put("phone", personnel.getPhone());
        result.put("idCard", personnel.getIdCard());
        result.put("status", personnel.getStatus());
        result.put("hireDate", personnel.getHireDate());
        result.put("createTime", personnel.getCreateTime());
        result.put("faceRegistered", faceDataList.size() > 0 ? 1 : 0);
        result.put("faceDataCount", faceDataList.size());
        result.put("faceDataList", faceDataList);

        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Personnel personnel) {
        log.info("更新人员, ID: {}, 数据: {}", id, personnel);
        personnel.setId(id);
        personnelService.updateById(personnel);
        return Result.success(null, "更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除人员, ID: {}", id);
        try {
            boolean result = personnelService.removeById(id);
            log.info("删除结果: {}, 影响行数: {}", result, result ? 1 : 0);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            log.error("删除失败: {}", e.getMessage());
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/toggleStatus/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        log.info("切换状态, ID: {}", id);
        personnelService.toggleStatus(id);
        return Result.success(null, "状态切换成功");
    }
}
