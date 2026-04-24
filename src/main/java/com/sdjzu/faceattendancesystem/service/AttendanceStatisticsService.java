package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.AttendanceStatistics;

import java.util.List;
import java.util.Map;

/**
 * 考勤统计服务接口
 */
public interface AttendanceStatisticsService extends IService<AttendanceStatistics> {

    /**
     * 获取考勤统计列表
     */
    IPage<AttendanceStatistics> pageList(String department, Integer year, Integer month, Integer current, Integer size);

    /**
     * 统计今日考勤数据
     */
    Map<String, Object> getTodayStatistics();

    /**
     * 统计井下人数
     */
    Integer getUndergroundCount();

    /**
     * 获取每日入井人数趋势
     */
    List<Map<String, Object>> getDailyTrend(Integer days);

    /**
     * 获取部门出勤率排行
     */
    List<Map<String, Object>> getDepartmentAttendanceRank();

    /**
     * 生成月度统计
     */
    void generateMonthlyStatistics(Integer year, Integer month);

    /**
     * 获取考勤异常统计
     */
    List<Map<String, Object>> getExceptionStatistics();

    /**
     * 获取井下人员分布统计
     */
    List<Map<String, Object>> getDistributionStatistics();
}
