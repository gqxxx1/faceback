package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.PageResult;
import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.AttendanceStatistics;
import com.sdjzu.faceattendancesystem.service.AttendanceStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考勤统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private AttendanceStatisticsService statisticsService;

    @GetMapping("/page")
    public Result<PageResult<AttendanceStatistics>> page(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        var page = statisticsService.pageList(department, year, month, current, size);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(statisticsService.getTodayStatistics());
    }

    @GetMapping("/underground/count")
    public Result<Integer> getUndergroundCount() {
        return Result.success(statisticsService.getUndergroundCount());
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getDailyTrend(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(statisticsService.getDailyTrend(days));
    }

    @GetMapping("/department/rank")
    public Result<List<Map<String, Object>>> getDepartmentRank() {
        return Result.success(statisticsService.getDepartmentAttendanceRank());
    }

    @GetMapping("/exception")
    public Result<List<Map<String, Object>>> getExceptionStatistics() {
        return Result.success(statisticsService.getExceptionStatistics());
    }

    @GetMapping("/distribution")
    public Result<List<Map<String, Object>>> getDistributionStatistics() {
        return Result.success(statisticsService.getDistributionStatistics());
    }
}
