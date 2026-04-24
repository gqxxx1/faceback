package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdjzu.faceattendancesystem.entity.FaceData;
import com.sdjzu.faceattendancesystem.mapper.FaceDataMapper;
import com.sdjzu.faceattendancesystem.service.FaceDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人脸数据服务实现类
 */
@Service
public class FaceDataServiceImpl extends ServiceImpl<FaceDataMapper, FaceData> implements FaceDataService {

    @Override
    @Transactional
    public boolean saveFaceData(FaceData faceData) {
        // 设置默认值
        if (faceData.getIsValid() == null) {
            faceData.setIsValid(1);
        }
        if (faceData.getCaptureTime() == null) {
            faceData.setCaptureTime(LocalDateTime.now());
        }
        if (faceData.getIsPrimary() == null) {
            faceData.setIsPrimary(0);
        }
        if (faceData.getDeleted() == null) {
            faceData.setDeleted(0);
        }

        // 检查是否需要设为主图（第一张自动设为主图）
        LambdaQueryWrapper<FaceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaceData::getPersonnelId, faceData.getPersonnelId())
               .eq(FaceData::getDeleted, 0)
               .eq(FaceData::getIsValid, 1);
        long count = this.count(wrapper);

        if (count == 0) {
            faceData.setIsPrimary(1);
        }

        return this.save(faceData);
    }

    @Override
    public List<FaceData> getByPersonnelId(Long personnelId) {
        LambdaQueryWrapper<FaceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaceData::getPersonnelId, personnelId)
               .eq(FaceData::getDeleted, 0)
               .eq(FaceData::getIsValid, 1)
               .orderByDesc(FaceData::getIsPrimary)
               .orderByDesc(FaceData::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public boolean deleteByPersonnelId(Long personnelId) {
        LambdaQueryWrapper<FaceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaceData::getPersonnelId, personnelId);
        return this.remove(wrapper);
    }

    @Override
    public int getValidFaceCount(Long personnelId) {
        LambdaQueryWrapper<FaceData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaceData::getPersonnelId, personnelId)
               .eq(FaceData::getDeleted, 0)
               .eq(FaceData::getIsValid, 1);
        return (int) this.count(wrapper);
    }

    @Override
    @Transactional
    public boolean setPrimary(Long faceDataId, Long personnelId) {
        // 先取消该人员的所有主图标记
        LambdaQueryWrapper<FaceData> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(FaceData::getPersonnelId, personnelId)
                     .eq(FaceData::getIsPrimary, 1);
        List<FaceData> primaryList = this.list(updateWrapper);
        for (FaceData face : primaryList) {
            face.setIsPrimary(0);
            this.updateById(face);
        }

        // 设置新主图
        FaceData faceData = this.getById(faceDataId);
        if (faceData != null) {
            faceData.setIsPrimary(1);
            return this.updateById(faceData);
        }
        return false;
    }

    @Override
    public IPage<FaceData> pageList(FaceData query, Integer current, Integer size) {
        Page<FaceData> page = new Page<>(current, size);
        LambdaQueryWrapper<FaceData> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            if (query.getPersonnelId() != null) {
                wrapper.eq(FaceData::getPersonnelId, query.getPersonnelId());
            }
            if (query.getEmployeeId() != null) {
                wrapper.eq(FaceData::getEmployeeId, query.getEmployeeId());
            }
            if (query.getIsValid() != null) {
                wrapper.eq(FaceData::getIsValid, query.getIsValid());
            }
            if (query.getIsPrimary() != null) {
                wrapper.eq(FaceData::getIsPrimary, query.getIsPrimary());
            }
        }

        wrapper.eq(FaceData::getDeleted, 0);
        wrapper.orderByDesc(FaceData::getCreateTime);

        return this.page(page, wrapper);
    }
}
