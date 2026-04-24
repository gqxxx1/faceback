package com.sdjzu.faceattendancesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdjzu.faceattendancesystem.entity.FaceCaptureTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人脸采集任务Mapper接口
 */
@Mapper
public interface FaceCaptureTaskMapper extends BaseMapper<FaceCaptureTask> {
}
