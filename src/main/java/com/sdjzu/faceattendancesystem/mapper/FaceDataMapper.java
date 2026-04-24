package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.FaceData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人脸数据Mapper接口
 */
@Mapper
public interface FaceDataMapper extends BaseMapper<FaceData> {
}
