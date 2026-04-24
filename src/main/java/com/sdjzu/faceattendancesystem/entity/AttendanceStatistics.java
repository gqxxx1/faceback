package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考勤统计实体类（按月汇总）
 */
@Data
@TableName("attendance_statistics")
public class AttendanceStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 人员ID
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
     * 部门
     */
    private String department;

    /**
     * 统计年份
     */
    private Integer statisticsYear;

    /**
     * 统计月份
     */
    private Integer statisticsMonth;

    /**
     * 应出勤天数
     */
    private Integer shouldWorkDays;

    /**
     * 实际出勤天数
     */
    private Integer actualWorkDays;

    /**
     * 迟到次数
     */
    private Integer lateCount;

    /**
     * 早退次数
     */
    private Integer earlyLeaveCount;

    /**
     * 缺勤次数
     */
    private Integer absentCount;

    /**
     * 加班时长（小时）
     */
    private Double overtimeHours;

    /**
     * 总工时（小时）
     */
    private Double totalHours;

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
