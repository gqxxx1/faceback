package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.AttendanceStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤统计 Mapper 接口
 */
@Mapper
public interface AttendanceStatisticsMapper extends BaseMapper<AttendanceStatistics> {
}
