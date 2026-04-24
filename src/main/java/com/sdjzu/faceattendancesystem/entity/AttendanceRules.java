package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考勤规则实体类
 */
@Data
@TableName("attendance_rules")
public class AttendanceRules implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 工作日开始时间 (HH:mm)
     */
    private String workStartTime;

    /**
     * 工作日结束时间 (HH:mm)
     */
    private String workEndTime;

    /**
     * 迟到阈值（分钟）
     */
    private Integer lateThreshold;

    /**
     * 早退阈值（分钟）
     */
    private Integer earlyLeaveThreshold;

    /**
     * 超长工时阈值（小时）
     */
    private Integer overtimeThreshold;

    /**
     * 连续工作天数阈值
     */
    private Integer continuousWorkThreshold;

    /**
     * 是否自动计算出勤天数
     */
    private Boolean autoCalculateWorkDays;

    /**
     * 是否自动计算加班时长
     */
    private Boolean autoCalculateOvertime;

    /**
     * 是否异常考勤自动报警
     */
    private Boolean autoAlarmException;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

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
