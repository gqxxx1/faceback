package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdjzu.faceattendancesystem.entity.FaceCaptureTask;

/**
 * 人脸采集任务服务接口
 */
public interface FaceCaptureTaskService {

    /**
     * 创建采集任务
     * @param task 任务信息
     * @return 任务ID
     */
    Long createTask(FaceCaptureTask task);

    /**
     * 更新任务采集进度
     * @param taskId 任务ID
     * @param capturedSamples 已采集样本数
     * @return 是否更新成功
     */
    boolean updateProgress(Long taskId, Integer capturedSamples);

    /**
     * 完成采集任务
     * @param taskId 任务ID
     * @return 是否完成成功
     */
    boolean completeTask(Long taskId);

    /**
     * 失败采集任务
     * @param taskId 任务ID
     * @param reason 失败原因
     * @return 是否设置成功
     */
    boolean failTask(Long taskId, String reason);

    /**
     * 根据人员ID获取任务
     * @param personnelId 人员ID
     * @return 任务信息
     */
    FaceCaptureTask getByPersonnelId(Long personnelId);

    /**
     * 获取任务分页
     * @param query 查询条件
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<FaceCaptureTask> pageList(FaceCaptureTask query, Integer current, Integer size);
}
