package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.SysLog;

/**
 * 系统日志服务接口
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 记录日志
     */
    void log(String username, String operationType, String operationDesc, String requestMethod,
             String requestUrl, String requestParams, String ipAddress, Integer status, String errorMsg);

    /**
     * 分页查询日志
     */
    IPage<SysLog> pageList(String username, String operationType, String operationTime, Integer current, Integer size);
}
