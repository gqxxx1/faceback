package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdjzu.faceattendancesystem.entity.FaceCaptureTask;
import com.sdjzu.faceattendancesystem.mapper.FaceCaptureTaskMapper;
import com.sdjzu.faceattendancesystem.service.FaceCaptureTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 人脸采集任务服务实现类
 */
@Service
public class FaceCaptureTaskServiceImpl implements FaceCaptureTaskService {

    @Autowired
    private FaceCaptureTaskMapper taskMapper;

    // 任务状态常量
    public static final int STATUS_PENDING = 0;   // 待采集
    public static final int STATUS_CAPTURING = 1; // 采集中
    public static final int STATUS_COMPLETED = 2; // 已完成
    public static final int STATUS_FAILED = 3;    // 已失败

    // 任务类型常量
    public static final int TYPE_FIRST_REGISTER = 1; // 首次录入
    public static final int TYPE_UPDATE = 2;          // 更新人脸
    public static final int TYPE_SUPPLEMENT = 3;     // 补录人脸

    @Override
    @Transactional
    public Long createTask(FaceCaptureTask task) {
        task.setTaskStatus(STATUS_PENDING);
        task.setCapturedSamples(0);
        task.setCreateTime(LocalDateTime.now());
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    @Transactional
    public boolean updateProgress(Long taskId, Integer capturedSamples) {
        FaceCaptureTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setCapturedSamples(capturedSamples);
            task.setTaskStatus(STATUS_CAPTURING);
            if (task.getStartTime() == null) {
                task.setStartTime(LocalDateTime.now());
            }
            return taskMapper.updateById(task) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean completeTask(Long taskId) {
        FaceCaptureTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setTaskStatus(STATUS_COMPLETED);
            task.setEndTime(LocalDateTime.now());
            return taskMapper.updateById(task) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean failTask(Long taskId, String reason) {
        FaceCaptureTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setTaskStatus(STATUS_FAILED);
            task.setEndTime(LocalDateTime.now());
            task.setRemark(reason);
            return taskMapper.updateById(task) > 0;
        }
        return false;
    }

    @Override
    public FaceCaptureTask getByPersonnelId(Long personnelId) {
        LambdaQueryWrapper<FaceCaptureTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaceCaptureTask::getPersonnelId, personnelId)
               .orderByDesc(FaceCaptureTask::getCreateTime)
               .last("LIMIT 1");
        return taskMapper.selectOne(wrapper);
    }

    @Override
    public IPage<FaceCaptureTask> pageList(FaceCaptureTask query, Integer current, Integer size) {
        Page<FaceCaptureTask> page = new Page<>(current, size);
        LambdaQueryWrapper<FaceCaptureTask> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            if (query.getPersonnelId() != null) {
                wrapper.eq(FaceCaptureTask::getPersonnelId, query.getPersonnelId());
            }
            if (query.getEmployeeId() != null) {
                wrapper.like(FaceCaptureTask::getEmployeeId, query.getEmployeeId());
            }
            if (query.getName() != null) {
                wrapper.like(FaceCaptureTask::getName, query.getName());
            }
            if (query.getTaskStatus() != null) {
                wrapper.eq(FaceCaptureTask::getTaskStatus, query.getTaskStatus());
            }
        }

        wrapper.orderByDesc(FaceCaptureTask::getCreateTime);
        return taskMapper.selectPage(page, wrapper);
    }
}
