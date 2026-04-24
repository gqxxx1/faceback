package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人员档案实体类
 */
@Data
@TableName("personnel")
public class Personnel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 人员ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工号
     */
    private String employeeId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：男/女
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 是否已录入人脸：0-未录入，1-已录入
     */
    private Integer faceRegistered;

    /**
     * 人脸样本数量
     */
    private Integer faceDataCount;

    /**
     * 状态：0-离职，1-在职
     */
    private Integer status;

    /**
     * 入职日期
     */
    private LocalDateTime hireDate;

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

    /**
     * 物理删除：直接删除记录
     */
    // @TableLogic  // 注释掉逻辑删除，改为物理删除
    private Integer deleted;
}
