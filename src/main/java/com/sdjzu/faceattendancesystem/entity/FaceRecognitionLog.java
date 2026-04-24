package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人脸识别日志实体类
 * 记录每次人脸识别操作
 */
@Data
@TableName("face_recognition_log")
public class FaceRecognitionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 识别出的人员ID
     */
    private Long personnelId;

    /**
     * 识别出的工号
     */
    private String employeeId;

    /**
     * 识别出的姓名
     */
    private String name;

    /**
     * 抓拍的原始图像
     */
    private byte[] capturedImage;

    /**
     * 匹配到的人脸数据ID
     */
    private Long matchedFaceId;

    /**
     * 识别置信度
     */
    private Double confidence;

    /**
     * 匹配阈值
     */
    private Double matchThreshold;

    /**
     * 识别结果：0-失败，1-成功
     */
    private Integer recognitionResult;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 识别地点
     */
    private String location;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 识别时间
     */
    private LocalDateTime recognitionTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
