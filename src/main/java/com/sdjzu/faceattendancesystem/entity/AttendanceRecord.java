package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考勤记录实体类
 */
@Data
@TableName("attendance_record")
public class AttendanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
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
     * 考勤类型：1-入井，2-出井
     */
    private Integer recordType;

    /**
     * 考勤时间
     */
    private LocalDateTime recordTime;

    /**
     * 考勤地点/区域
     */
    private String location;

    /**
     * 考勤方式：1-人脸识别，2-手动登记
     */
    private Integer attendanceMethod;

    /**
     * 人脸识别置信度
     */
    private Double confidence;

    /**
     * 是否异常：0-正常，1-异常
     */
    private Integer isAbnormal;

    /**
     * 异常原因
     */
    private String abnormalReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
