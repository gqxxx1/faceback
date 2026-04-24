package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤记录 Mapper 接口
 */
@Mapper
public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecord> {
}
