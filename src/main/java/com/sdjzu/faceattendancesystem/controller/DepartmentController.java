package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.Department;
import com.sdjzu.faceattendancesystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public Result<List<Department>> list() {
        return Result.success(departmentService.getAllDepartments());
    }
}
