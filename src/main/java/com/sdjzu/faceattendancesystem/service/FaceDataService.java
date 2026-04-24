package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdjzu.faceattendancesystem.entity.FaceData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 人脸数据服务接口
 */
public interface FaceDataService extends IService<FaceData> {

    /**
     * 保存人脸数据（包含图像处理）
     * @param faceData 人脸数据
     * @return 是否保存成功
     */
    boolean saveFaceData(FaceData faceData);

    /**
     * 根据人员ID获取人脸数据列表
     * @param personnelId 人员ID
     * @return 人脸数据列表
     */
    List<FaceData> getByPersonnelId(Long personnelId);

    /**
     * 根据人员ID删除所有人脸数据
     * @param personnelId 人员ID
     * @return 是否删除成功
     */
    boolean deleteByPersonnelId(Long personnelId);

    /**
     * 获取人员有效人脸数量
     * @param personnelId 人员ID
     * @return 有效人脸数量
     */
    int getValidFaceCount(Long personnelId);

    /**
     * 设置主图
     * @param faceDataId 人脸数据ID
     * @param personnelId 人员ID
     * @return 是否设置成功
     */
    boolean setPrimary(Long faceDataId, Long personnelId);

    /**
     * 人脸数据分页查询
     * @param query 查询条件
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<FaceData> pageList(FaceData query, Integer current, Integer size);
}
