package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.PageResult;
import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.SysLog;
import com.sdjzu.faceattendancesystem.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统日志控制器
 */
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/page")
    public Result<PageResult<SysLog>> page(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String operationTime,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        var page = sysLogService.pageList(username, operationType, operationTime, current, size);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }
}
