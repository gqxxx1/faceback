package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.AttendanceRules;

/**
 * 考勤规则服务接口
 */
public interface AttendanceRulesService extends IService<AttendanceRules> {

    /**
     * 获取当前启用的规则
     */
    AttendanceRules getActiveRules();

    /**
     * 保存规则
     */
    boolean saveRules(AttendanceRules rules);
}
