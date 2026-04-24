package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.entity.AttendanceRules;
import com.sdjzu.faceattendancesystem.service.AttendanceRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 考勤规则控制器
 */
@RestController
@RequestMapping("/api/rules")
public class RulesController {

    @Autowired
    private AttendanceRulesService rulesService;

    @GetMapping("/current")
    public Result<AttendanceRules> getCurrentRules() {
        return Result.success(rulesService.getActiveRules());
    }

    @PostMapping
    public Result<Void> saveRules(@RequestBody AttendanceRules rules) {
        rulesService.saveRules(rules);
        return Result.success(null, "规则保存成功");
    }
}
