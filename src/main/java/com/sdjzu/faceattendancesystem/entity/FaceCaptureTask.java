package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人脸采集任务实体类
 * 管理人脸采集流程
 */
@Data
@TableName("face_capture_task")
public class FaceCaptureTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联人员ID
     */
    private Long personnelId;

    /**
     * 工号
     */
    private String employeeId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 需要采集的样本数量
     */
    private Integer requiredSamples;

    /**
     * 已采集的样本数量
     */
    private Integer capturedSamples;

    /**
     * 任务状态：0-待采集，1-采集中，2-已完成，3-已失败
     */
    private Integer taskStatus;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime endTime;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 任务类型：1-首次录入，2-更新人脸，3-补录人脸
     */
    private Integer taskType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
