package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.AttendanceRecord;
import com.sdjzu.faceattendancesystem.service.AttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤记录控制器
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @PostMapping("/checkIn")
    public Result<AttendanceRecord> checkIn(@RequestParam Long personnelId,
                                            @RequestParam(required = false, defaultValue = "矿井入口") String location) {
        try {
            AttendanceRecord record = attendanceRecordService.checkIn(personnelId, location);
            return Result.success(record, "入井成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/checkOut")
    public Result<AttendanceRecord> checkOut(@RequestParam Long personnelId,
                                             @RequestParam(required = false, defaultValue = "矿井出口") String location) {
        try {
            AttendanceRecord record = attendanceRecordService.checkOut(personnelId, location);
            return Result.success(record, "出井成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/underground")
    public Result<List<AttendanceRecord>> getUndergroundPersonnel() {
        return Result.success(attendanceRecordService.getCurrentUndergroundPersonnel());
    }

    @GetMapping("/records")
    public Result<List<AttendanceRecord>> getRecords(@RequestParam(required = false) Long personnelId) {
        return Result.success(attendanceRecordService.getTodayRecords());
    }
}
