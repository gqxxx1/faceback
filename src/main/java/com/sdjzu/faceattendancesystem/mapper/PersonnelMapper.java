package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.Personnel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人员档案 Mapper 接口
 */
@Mapper
public interface PersonnelMapper extends BaseMapper<Personnel> {
}
