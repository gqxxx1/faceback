package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdjzu.faceattendancesystem.entity.AttendanceRules;
import com.sdjzu.faceattendancesystem.mapper.AttendanceRulesMapper;
import com.sdjzu.faceattendancesystem.service.AttendanceRulesService;
import org.springframework.stereotype.Service;

@Service
public class AttendanceRulesServiceImpl extends ServiceImpl<AttendanceRulesMapper, AttendanceRules>
        implements AttendanceRulesService {

    @Override
    public AttendanceRules getActiveRules() {
        LambdaQueryWrapper<AttendanceRules> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceRules::getStatus, 1);
        AttendanceRules rules = this.getOne(wrapper);

        if (rules == null) {
            // 返回默认规则
            rules = new AttendanceRules();
            rules.setRuleName("默认考勤规则");
            rules.setWorkStartTime("08:00");
            rules.setWorkEndTime("18:00");
            rules.setLateThreshold(10);
            rules.setEarlyLeaveThreshold(10);
            rules.setOvertimeThreshold(10);
            rules.setContinuousWorkThreshold(7);
            rules.setAutoCalculateWorkDays(true);
            rules.setAutoCalculateOvertime(true);
            rules.setAutoAlarmException(true);
            rules.setStatus(1);
        }

        return rules;
    }

    @Override
    public boolean saveRules(AttendanceRules rules) {
        // 先禁用所有规则
        LambdaQueryWrapper<AttendanceRules> wrapper = new LambdaQueryWrapper<>();
        AttendanceRules oldRules = this.getOne(wrapper);
        if (oldRules != null) {
            oldRules.setStatus(0);
            this.updateById(oldRules);
        }

        // 保存新规则
        rules.setId(null);
        rules.setStatus(1);
        return this.save(rules);
    }
}
