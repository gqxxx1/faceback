package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.AttendanceRules;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤规则 Mapper 接口
 */
@Mapper
public interface AttendanceRulesMapper extends BaseMapper<AttendanceRules> {
}
