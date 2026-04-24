package com.sdjzu.faceattendancesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人脸数据实体类
 * 存储OpenCV处理后的人脸图像和特征数据
 */
@Data
@TableName("face_data")
public class FaceData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 人脸数据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联人员ID
     */
    private Long personnelId;

    /**
     * 工号（冗余字段，便于查询）
     */
    private String employeeId;

    /**
     * 灰度处理后的人脸图像数据（OpenCV处理后的灰度图，MEDIUMBLOB）
     */
    private byte[] faceImage;

    /**
     * 人脸图片存储路径（备选，存储原始图或处理后图）
     */
    private String faceImagePath;

    /**
     * 人脸特征向量（JSON格式，存储OpenCV/LBPH特征）
     */
    private String faceFeature;

    /**
     * 人脸描述符（128维浮点数序列，用于人脸比对）
     */
    private String faceDescriptor;

    /**
     * 图像宽度（像素）
     */
    private Integer imageWidth;

    /**
     * 图像高度（像素）
     */
    private Integer imageHeight;

    /**
     * 采集设备
     */
    private String captureDevice;

    /**
     * 采集地点
     */
    private String captureLocation;

    /**
     * 采集角度（正面、左侧、右侧等）
     */
    private String captureAngle;

    /**
     * 图像质量评分（0-1之间）
     */
    private Double imageQualityScore;

    /**
     * 是否有效：0-无效，1-有效
     */
    private Integer isValid;

    /**
     * 是否为主图：0-否，1-是（用于识别时优先使用）
     */
    private Integer isPrimary;

    /**
     * 采集时间
     */
    private LocalDateTime captureTime;

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
     * 逻辑删除：0-未删除，1-已删除
     */
    private Integer deleted;
}
