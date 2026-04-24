-- ============================================
-- 矿井人员考勤系统 - 基础数据初始化脚本
-- 仅包含管理员、部门、考勤规则等基础数据（不含人员人脸信息）
-- ============================================

USE face_attendance;

-- ============================================
-- 部门数据
-- ============================================
INSERT INTO department (department_name, department_code, description, leader, phone, status) VALUES
('综合管理部', 'ADMIN01', '综合管理与行政协调', '张明', '13800000001', 1),
('采煤一队', 'CC01', '采煤作业一队', '李强', '13800000002', 1),
('采煤二队', 'CC02', '采煤作业二队', '王磊', '13800000003', 1),
('掘进一队', 'JJ01', '掘进作业一队', '赵刚', '13800000004', 1),
('掘进二队', 'JJ02', '掘进作业二队', '孙伟', '13800000005', 1),
('通风队', 'TF01', '通风安全保障', '周涛', '13800000006', 1),
('机电队', 'JD01', '机电设备维护', '吴军', '13800000007', 1),
('运输队', 'YS01', '井下运输作业', '郑海', '13800000008', 1),
('安全监察部', 'AQ01', '安全监督检查', '刘洋', '13800000009', 1),
('技术保障部', 'JS01', '技术支持与维护', '陈峰', '13800000010', 1);

-- ============================================
-- 用户数据（管理员/考勤员）
-- ============================================
INSERT INTO sys_user (username, password, real_name, phone, email, role, status) VALUES
('admin', 'admin123', '系统管理员', '13900000001', 'admin@mine.com', 'admin', 1),
('admin2', 'admin123', '运维管理员', '13900000002', 'admin2@mine.com', 'admin', 1),
('attendance01', 'admin123', '考勤员-张丽', '13900000003', 'att01@mine.com', 'attendance', 1),
('attendance02', 'admin123', '考勤员-李娜', '13900000004', 'att02@mine.com', 'attendance', 1),
('attendance03', 'admin123', '考勤员-王芳', '13900000005', 'att03@mine.com', 'attendance', 1);

-- ============================================
-- 考勤规则
-- ============================================
INSERT INTO attendance_rules (rule_name, work_start_time, work_end_time, late_threshold, early_leave_threshold, overtime_threshold, continuous_work_threshold, auto_calculate_work_days, auto_calculate_overtime, auto_alarm_exception, status) VALUES
('标准考勤规则', '08:00:00', '18:00:00', 10, 10, 4, 7, 1, 1, 1, 1),
('早班规则', '06:00:00', '14:00:00', 10, 10, 2, 7, 1, 1, 1, 1),
('中班规则', '14:00:00', '22:00:00', 10, 10, 2, 7, 1, 1, 1, 1),
('夜班规则', '22:00:00', '06:00:00', 10, 10, 4, 7, 1, 1, 1, 1);

-- ============================================
-- 验证数据
-- ============================================
SELECT '=== 基础数据统计 ===' AS info;
SELECT '部门数量' AS table_name, COUNT(*) AS count FROM department WHERE deleted = 0
UNION ALL SELECT '用户数量', COUNT(*) FROM sys_user WHERE deleted = 0
UNION ALL SELECT '考勤规则', COUNT(*) FROM attendance_rules WHERE deleted = 0;