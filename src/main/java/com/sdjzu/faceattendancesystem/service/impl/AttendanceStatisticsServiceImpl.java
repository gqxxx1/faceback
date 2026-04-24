package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdjzu.faceattendancesystem.entity.AttendanceRecord;
import com.sdjzu.faceattendancesystem.entity.AttendanceStatistics;
import com.sdjzu.faceattendancesystem.entity.Personnel;
import com.sdjzu.faceattendancesystem.mapper.AttendanceRecordMapper;
import com.sdjzu.faceattendancesystem.mapper.AttendanceStatisticsMapper;
import com.sdjzu.faceattendancesystem.service.AttendanceRecordService;
import com.sdjzu.faceattendancesystem.service.AttendanceStatisticsService;
import com.sdjzu.faceattendancesystem.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceStatisticsServiceImpl extends ServiceImpl<AttendanceStatisticsMapper, AttendanceStatistics>
        implements AttendanceStatisticsService {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @Autowired
    private PersonnelService personnelService;

    @Override
    public IPage<AttendanceStatistics> pageList(String department, Integer year, Integer month, Integer current, Integer size) {
        Page<AttendanceStatistics> page = new Page<>(current, size);
        LambdaQueryWrapper<AttendanceStatistics> wrapper = new LambdaQueryWrapper<>();

        if (year != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsYear, year);
        }
        if (month != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsMonth, month);
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq(AttendanceStatistics::getDepartment, department);
        }

        wrapper.orderByDesc(AttendanceStatistics::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Map<String, Object> getTodayStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 今日井下人数
        List<AttendanceRecord> underground = attendanceRecordService.getCurrentUndergroundPersonnel();
        stats.put("undergroundCount", underground.size());

        // 今日出勤人数
        List<Personnel> allPersonnel = personnelService.list();
        stats.put("todayAttendance", (int) allPersonnel.stream().filter(p -> p.getStatus() == 1).count());

        // 今日考勤异常数
        List<AttendanceRecord> todayRecords = attendanceRecordService.getTodayRecords();
        long abnormalCount = todayRecords.stream().filter(r -> r.getIsAbnormal() == 1).count();
        stats.put("abnormalCount", abnormalCount);

        // 本月出勤天数（需要计算）
        stats.put("monthAttendanceDays", 22);

        return stats;
    }

    @Override
    public Integer getUndergroundCount() {
        return attendanceRecordService.getCurrentUndergroundPersonnel().size();
    }

    @Override
    public List<Map<String, Object>> getDailyTrend(Integer days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendanceRecord::getRecordType, 1)
                    .ge(AttendanceRecord::getRecordTime, startOfDay)
                    .lt(AttendanceRecord::getRecordTime, endOfDay);

            long count = attendanceRecordService.count(wrapper);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("count", count);
            trend.add(dayData);
        }

        return trend;
    }

    @Override
    public List<Map<String, Object>> getDepartmentAttendanceRank() {
        // 按部门统计出勤率
        List<Personnel> allPersonnel = personnelService.list();
        Map<String, Long> departmentCount = allPersonnel.stream()
                .filter(p -> p.getStatus() == 1)
                .collect(Collectors.groupingBy(Personnel::getDepartment, Collectors.counting()));

        List<Map<String, Object>> rank = new ArrayList<>();
        departmentCount.forEach((dept, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("department", dept);
            item.put("count", count);
            item.put("rate", 95.0 + new Random().nextInt(5)); // 模拟数据
            rank.add(item);
        });

        return rank;
    }

    @Override
    public void generateMonthlyStatistics(Integer year, Integer month) {
        // 获取当月所有人员
        List<Personnel> allPersonnel = personnelService.list();
        LocalDateTime monthStart = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        for (Personnel personnel : allPersonnel) {
            // 查询该人员当月的入井记录
            LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendanceRecord::getPersonnelId, personnel.getId())
                    .eq(AttendanceRecord::getRecordType, 1)
                    .ge(AttendanceRecord::getRecordTime, monthStart)
                    .lt(AttendanceRecord::getRecordTime, monthEnd);

            List<AttendanceRecord> checkInRecords = attendanceRecordService.list(wrapper);

            // 计算出勤天数（去重）
            Set<LocalDate> workDays = checkInRecords.stream()
                    .map(r -> r.getRecordTime().toLocalDate())
                    .collect(Collectors.toSet());

            // 统计异常
            LambdaQueryWrapper<AttendanceRecord> abnormalWrapper = new LambdaQueryWrapper<>();
            abnormalWrapper.eq(AttendanceRecord::getPersonnelId, personnel.getId())
                    .eq(AttendanceRecord::getIsAbnormal, 1)
                    .ge(AttendanceRecord::getRecordTime, monthStart)
                    .lt(AttendanceRecord::getRecordTime, monthEnd);
            long abnormalCount = attendanceRecordService.count(abnormalWrapper);

            // 保存或更新统计记录
            LambdaQueryWrapper<AttendanceStatistics> statsWrapper = new LambdaQueryWrapper<>();
            statsWrapper.eq(AttendanceStatistics::getPersonnelId, personnel.getId())
                    .eq(AttendanceStatistics::getStatisticsYear, year)
                    .eq(AttendanceStatistics::getStatisticsMonth, month);

            AttendanceStatistics existing = this.getOne(statsWrapper);
            if (existing != null) {
                existing.setActualWorkDays(workDays.size());
                existing.setAbsentCount(22 - workDays.size());
                existing.setOvertimeHours((double) workDays.size() * 2);
                existing.setTotalHours((double) workDays.size() * 8);
                this.updateById(existing);
            } else {
                AttendanceStatistics stats = new AttendanceStatistics();
                stats.setPersonnelId(personnel.getId());
                stats.setEmployeeId(personnel.getEmployeeId());
                stats.setName(personnel.getName());
                stats.setDepartment(personnel.getDepartment());
                stats.setStatisticsYear(year);
                stats.setStatisticsMonth(month);
                stats.setShouldWorkDays(22);
                stats.setActualWorkDays(workDays.size());
                stats.setLateCount((int) (abnormalCount / 3));
                stats.setEarlyLeaveCount((int) (abnormalCount / 3));
                stats.setAbsentCount(22 - workDays.size());
                stats.setOvertimeHours((double) workDays.size() * 2);
                stats.setTotalHours((double) workDays.size() * 8);
                this.save(stats);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getExceptionStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取今日记录统计异常类型
        List<AttendanceRecord> todayRecords = attendanceRecordService.getTodayRecords();
        List<AttendanceRecord> abnormalRecords = todayRecords.stream()
                .filter(r -> r.getIsAbnormal() == 1)
                .collect(Collectors.toList());

        // 按异常类型分组统计
        Map<String, Long> exceptionCount = abnormalRecords.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getAbnormalReason() != null ? r.getAbnormalReason() : "其他异常",
                        Collectors.counting()
                ));

        // 如果没有异常数据，返回提示
        if (exceptionCount.isEmpty()) {
            Map<String, Object> noData = new HashMap<>();
            noData.put("name", "无异常");
            noData.put("value", 0);
            result.add(noData);
        } else {
            exceptionCount.forEach((name, count) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("value", count);
                result.add(item);
            });
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getDistributionStatistics() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取当前井下人员
        List<AttendanceRecord> underground = attendanceRecordService.getCurrentUndergroundPersonnel();

        // 按位置分组统计
        Map<String, Long> locationCount = underground.stream()
                .filter(r -> r.getLocation() != null && !r.getLocation().isEmpty())
                .collect(Collectors.groupingBy(AttendanceRecord::getLocation, Collectors.counting()));

        // 如果没有数据，返回提示
        if (locationCount.isEmpty()) {
            Map<String, Object> noData = new HashMap<>();
            noData.put("name", "暂无数据");
            noData.put("value", 0);
            result.add(noData);
        } else {
            locationCount.forEach((name, count) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("value", count);
                result.add(item);
            });
        }

        return result;
    }
}
