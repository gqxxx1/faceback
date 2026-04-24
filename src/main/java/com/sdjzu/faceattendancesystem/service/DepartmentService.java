package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.Department;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取所有部门
     */
    List<Department> getAllDepartments();
}
