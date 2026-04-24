package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.FaceRecognitionLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人脸识别日志Mapper接口
 */
@Mapper
public interface FaceRecognitionLogMapper extends BaseMapper<FaceRecognitionLog> {
}
