package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.AttendanceRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤记录服务接口
 */
public interface AttendanceRecordService extends IService<AttendanceRecord> {

    /**
     * 记录入井
     */
    AttendanceRecord checkIn(Long personnelId, String location);

    /**
     * 记录出井
     */
    AttendanceRecord checkOut(Long personnelId, String location);

    /**
     * 获取当前井下人员列表
     */
    List<AttendanceRecord> getCurrentUndergroundPersonnel();

    /**
     * 获取人员的考勤记录
     */
    List<AttendanceRecord> getRecordsByPersonnel(Long personnelId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取今日考勤记录
     */
    List<AttendanceRecord> getTodayRecords();
}
