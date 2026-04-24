package com.sdjzu.faceattendancesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdjzu.faceattendancesystem.entity.SysLog;
import com.sdjzu.faceattendancesystem.mapper.SysLogMapper;
import com.sdjzu.faceattendancesystem.service.SysLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public void log(String username, String operationType, String operationDesc, String requestMethod,
                    String requestUrl, String requestParams, String ipAddress, Integer status, String errorMsg) {
        SysLog sysLog = new SysLog();
        sysLog.setUsername(username);
        sysLog.setOperationType(operationType);
        sysLog.setOperationDesc(operationDesc);
        sysLog.setRequestMethod(requestMethod);
        sysLog.setRequestUrl(requestUrl);
        sysLog.setRequestParams(requestParams);
        sysLog.setIpAddress(ipAddress);
        sysLog.setStatus(status);
        sysLog.setErrorMsg(errorMsg);
        this.save(sysLog);
    }

    @Override
    public IPage<SysLog> pageList(String username, String operationType, String operationTime,
                                  Integer current, Integer size) {
        Page<SysLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.eq(SysLog::getUsername, username);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysLog::getOperationType, operationType);
        }

        wrapper.orderByDesc(SysLog::getOperationTime);
        return this.page(page, wrapper);
    }
}
