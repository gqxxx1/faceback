package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdjzu.faceattendancesystem.entity.AttendanceRecord;
import com.sdjzu.faceattendancesystem.entity.Personnel;
import com.sdjzu.faceattendancesystem.mapper.AttendanceRecordMapper;
import com.sdjzu.faceattendancesystem.service.AttendanceRecordService;
import com.sdjzu.faceattendancesystem.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttendanceRecordServiceImpl extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord>
        implements AttendanceRecordService {

    @Autowired
    private PersonnelService personnelService;

    @Override
    @Transactional
    public AttendanceRecord checkIn(Long personnelId, String location) {
        Personnel personnel = personnelService.getById(personnelId);
        if (personnel == null) {
            throw new RuntimeException("人员不存在");
        }

        AttendanceRecord record = new AttendanceRecord();
        record.setPersonnelId(personnelId);
        record.setEmployeeId(personnel.getEmployeeId());
        record.setName(personnel.getName());
        record.setDepartment(personnel.getDepartment());
        record.setRecordType(1); // 入井
        record.setRecordTime(LocalDateTime.now());
        record.setLocation(location);
        record.setAttendanceMethod(1); // 人脸识别
        record.setConfidence(0.99);
        record.setIsAbnormal(0);

        this.save(record);
        return record;
    }

    @Override
    @Transactional
    public AttendanceRecord checkOut(Long personnelId, String location) {
        Personnel personnel = personnelService.getById(personnelId);
        if (personnel == null) {
            throw new RuntimeException("人员不存在");
        }

        AttendanceRecord record = new AttendanceRecord();
        record.setPersonnelId(personnelId);
        record.setEmployeeId(personnel.getEmployeeId());
        record.setName(personnel.getName());
        record.setDepartment(personnel.getDepartment());
        record.setRecordType(2); // 出井
        record.setRecordTime(LocalDateTime.now());
        record.setLocation(location);
        record.setAttendanceMethod(1); // 人脸识别
        record.setConfidence(0.99);
        record.setIsAbnormal(0);

        this.save(record);
        return record;
    }

    @Override
    public List<AttendanceRecord> getCurrentUndergroundPersonnel() {
        // 获取所有有入井记录但没有出井记录的人员
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 获取今日入井记录
        LambdaQueryWrapper<AttendanceRecord> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(AttendanceRecord::getRecordType, 1)
                .ge(AttendanceRecord::getRecordTime, todayStart);
        List<AttendanceRecord> checkInRecords = this.list(checkInWrapper);

        // 获取今日出井记录的人员ID
        LambdaQueryWrapper<AttendanceRecord> checkOutWrapper = new LambdaQueryWrapper<>();
        checkOutWrapper.eq(AttendanceRecord::getRecordType, 2)
                .ge(AttendanceRecord::getRecordTime, todayStart);
        List<AttendanceRecord> checkOutRecords = this.list(checkOutWrapper);
        Set<Long> checkedOutPersonnelIds = checkOutRecords.stream()
                .map(AttendanceRecord::getPersonnelId)
                .collect(Collectors.toSet());

        // 过滤出还在井下的人员
        return checkInRecords.stream()
                .filter(record -> !checkedOutPersonnelIds.contains(record.getPersonnelId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> getRecordsByPersonnel(Long personnelId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceRecord::getPersonnelId, personnelId)
                .ge(AttendanceRecord::getRecordTime, startTime)
                .le(AttendanceRecord::getRecordTime, endTime)
                .orderByDesc(AttendanceRecord::getRecordTime);
        return this.list(wrapper);
    }

    @Override
    public List<AttendanceRecord> getTodayRecords() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(AttendanceRecord::getRecordTime, todayStart)
                .orderByDesc(AttendanceRecord::getRecordTime);
        return this.list(wrapper);
    }
}
