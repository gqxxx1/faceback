package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.Personnel;

import java.util.List;

/**
 * 人员服务接口
 */
public interface PersonnelService extends IService<Personnel> {

    /**
     * 分页查询人员列表
     */
    IPage<Personnel> pageList(Personnel query, Integer current, Integer size);

    /**
     * 根据姓名搜索
     */
    List<Personnel> searchByName(String name);

    /**
     * 根据工号搜索
     */
    Personnel getByEmployeeId(String employeeId);

    /**
     * 切换人员状态
     */
    boolean toggleStatus(Long id);
}
