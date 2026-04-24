package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdjzu.faceattendancesystem.entity.Personnel;
import com.sdjzu.faceattendancesystem.mapper.PersonnelMapper;
import com.sdjzu.faceattendancesystem.service.PersonnelService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PersonnelServiceImpl extends ServiceImpl<PersonnelMapper, Personnel> implements PersonnelService {

    @Override
    public IPage<Personnel> pageList(Personnel query, Integer current, Integer size) {
        Page<Personnel> page = new Page<>(current, size);
        LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            if (StringUtils.hasText(query.getName())) {
                wrapper.like(Personnel::getName, query.getName());
            }
            if (StringUtils.hasText(query.getEmployeeId())) {
                wrapper.eq(Personnel::getEmployeeId, query.getEmployeeId());
            }
            if (StringUtils.hasText(query.getDepartment())) {
                wrapper.eq(Personnel::getDepartment, query.getDepartment());
            }
            if (query.getStatus() != null) {
                wrapper.eq(Personnel::getStatus, query.getStatus());
            }
        }

        wrapper.orderByDesc(Personnel::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Personnel> searchByName(String name) {
        LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Personnel::getName, name);
        wrapper.eq(Personnel::getStatus, 1);
        return this.list(wrapper);
    }

    @Override
    public Personnel getByEmployeeId(String employeeId) {
        LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Personnel::getEmployeeId, employeeId);
        return this.getOne(wrapper);
    }

    @Override
    public boolean toggleStatus(Long id) {
        Personnel personnel = this.getById(id);
        if (personnel != null) {
            personnel.setStatus(personnel.getStatus() == 1 ? 0 : 1);
            return this.updateById(personnel);
        }
        return false;
    }
}
