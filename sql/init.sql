-- ============================================
-- 矿井人员考勤系统 - 数据库初始化脚本（完整版）
-- 包含：表结构、触发器、初始数据
-- 执行方式: source D:/桌面/FaceAttendanceSystem/sql/init.sql;
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS face_attendance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE face_attendance;

-- ============================================
-- 1. 部门表
-- ============================================
DROP TABLE IF EXISTS department;
CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    department_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    department_code VARCHAR(20) NOT NULL COMMENT '部门代码',
    description VARCHAR(255) COMMENT '部门描述',
    leader VARCHAR(50) COMMENT '部门负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ============================================
-- 2. 人员档案表
-- ============================================
DROP TABLE IF EXISTS personnel;
CREATE TABLE personnel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人员ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    department VARCHAR(50) COMMENT '部门',
    position VARCHAR(50) COMMENT '职位',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(20) COMMENT '身份证号',
    face_registered TINYINT(1) DEFAULT 0 COMMENT '是否已录入人脸：0-未录入，1-已录入',
    face_data_count INT DEFAULT 0 COMMENT '人脸样本数量',
    status INT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    hire_date DATETIME COMMENT '入职日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员档案表';

-- ============================================
-- 3. 人脸数据表
-- ============================================
DROP TABLE IF EXISTS face_data;
CREATE TABLE face_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人脸数据ID',
    personnel_id BIGINT NOT NULL COMMENT '关联人员ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '工号（冗余字段，便于查询）',
    face_image MEDIUMBLOB COMMENT '灰度处理后的人脸图像数据',
    face_image_path VARCHAR(255) COMMENT '人脸图片存储路径',
    face_feature LONGTEXT COMMENT '人脸特征向量（JSON格式）',
    face_descriptor VARCHAR(8000) COMMENT '人脸描述符（128维浮点数序列）',
    image_width INT COMMENT '图像宽度（像素）',
    image_height INT COMMENT '图像高度（像素）',
    capture_device VARCHAR(100) COMMENT '采集设备',
    capture_location VARCHAR(100) COMMENT '采集地点',
    capture_angle VARCHAR(50) COMMENT '采集角度（正面、左侧、右侧等）',
    image_quality_score DECIMAL(5,4) COMMENT '图像质量评分（0-1之间）',
    is_valid TINYINT(1) DEFAULT 1 COMMENT '是否有效：0-无效，1-有效',
    is_primary TINYINT(1) DEFAULT 0 COMMENT '是否为主图：0-否，1-是',
    capture_time DATETIME NOT NULL COMMENT '采集时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    CONSTRAINT fk_face_personnel FOREIGN KEY (personnel_id) REFERENCES personnel(id) ON DELETE CASCADE,
    INDEX idx_personnel_id (personnel_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_capture_time (capture_time),
    INDEX idx_is_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸数据表';

-- ============================================
-- 4. 用户表
-- ============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) DEFAULT 'employee' COMMENT '角色：admin-管理员，attendance-考勤员，employee-普通员工',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 5. 考勤记录表
-- ============================================
DROP TABLE IF EXISTS attendance_record;
CREATE TABLE attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    department VARCHAR(50) COMMENT '部门',
    record_type INT NOT NULL COMMENT '记录类型：1-入井，2-出井',
    record_time DATETIME NOT NULL COMMENT '记录时间',
    location VARCHAR(100) COMMENT '考勤地点',
    attendance_method INT DEFAULT 1 COMMENT '考勤方式：1-人脸识别，2-手动输入',
    confidence DECIMAL(5,4) COMMENT '识别置信度',
    face_data_id BIGINT COMMENT '关联人脸数据ID',
    is_abnormal INT DEFAULT 0 COMMENT '是否异常：0-正常，1-异常',
    abnormal_reason VARCHAR(255) COMMENT '异常原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_personnel_id (personnel_id),
    INDEX idx_record_time (record_time),
    INDEX idx_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- ============================================
-- 6. 考勤统计表
-- ============================================
DROP TABLE IF EXISTS attendance_statistics;
CREATE TABLE attendance_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    department VARCHAR(50) COMMENT '部门',
    statistics_year INT NOT NULL COMMENT '统计年份',
    statistics_month INT NOT NULL COMMENT '统计月份',
    should_work_days INT DEFAULT 22 COMMENT '应出勤天数',
    actual_work_days INT DEFAULT 0 COMMENT '实际出勤天数',
    late_count INT DEFAULT 0 COMMENT '迟到次数',
    early_leave_count INT DEFAULT 0 COMMENT '早退次数',
    absent_count INT DEFAULT 0 COMMENT '缺勤次数',
    overtime_hours DECIMAL(10,2) DEFAULT 0 COMMENT '加班时长(小时)',
    total_hours DECIMAL(10,2) DEFAULT 0 COMMENT '总工时(小时)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_personnel_month (personnel_id, statistics_year, statistics_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤统计表';

-- ============================================
-- 7. 考勤规则表
-- ============================================
DROP TABLE IF EXISTS attendance_rules;
CREATE TABLE attendance_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    rule_name VARCHAR(50) NOT NULL COMMENT '规则名称',
    work_start_time TIME NOT NULL COMMENT '工作开始时间',
    work_end_time TIME NOT NULL COMMENT '工作结束时间',
    late_threshold INT DEFAULT 10 COMMENT '迟到阈值(分钟)',
    early_leave_threshold INT DEFAULT 10 COMMENT '早退阈值(分钟)',
    overtime_threshold INT DEFAULT 10 COMMENT '超长工时阈值(小时)',
    continuous_work_threshold INT DEFAULT 7 COMMENT '连续工作天数阈值',
    auto_calculate_work_days TINYINT(1) DEFAULT 1 COMMENT '自动计算出勤天数',
    auto_calculate_overtime TINYINT(1) DEFAULT 1 COMMENT '自动计算加班时长',
    auto_alarm_exception TINYINT(1) DEFAULT 1 COMMENT '异常考勤自动报警',
    status INT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤规则表';

-- ============================================
-- 8. 系统日志表
-- ============================================
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    username VARCHAR(50) NOT NULL COMMENT '操作用户名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(255) COMMENT '操作描述',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(255) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    status INT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- ============================================
-- 9. 人脸识别日志表
-- ============================================
DROP TABLE IF EXISTS face_recognition_log;
CREATE TABLE face_recognition_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    personnel_id BIGINT COMMENT '识别出的人员ID',
    employee_id VARCHAR(50) COMMENT '识别出的工号',
    name VARCHAR(50) COMMENT '识别出的姓名',
    captured_image MEDIUMBLOB COMMENT '抓拍的原始图像',
    matched_face_id BIGINT COMMENT '匹配到的人脸数据ID',
    confidence DECIMAL(5,4) COMMENT '识别置信度',
    match_threshold DECIMAL(5,4) COMMENT '匹配阈值',
    recognition_result INT NOT NULL COMMENT '识别结果：0-失败，1-成功',
    failure_reason VARCHAR(255) COMMENT '失败原因',
    location VARCHAR(100) COMMENT '识别地点',
    device_info VARCHAR(100) COMMENT '设备信息',
    recognition_time DATETIME NOT NULL COMMENT '识别时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_personnel_id (personnel_id),
    INDEX idx_recognition_time (recognition_time),
    INDEX idx_recognition_result (recognition_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸识别日志表';

-- ============================================
-- 10. 人脸采集任务表
-- ============================================
DROP TABLE IF EXISTS face_capture_task;
CREATE TABLE face_capture_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    personnel_id BIGINT NOT NULL COMMENT '关联人员ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    required_samples INT DEFAULT 5 COMMENT '需要采集的样本数量',
    captured_samples INT DEFAULT 0 COMMENT '已采集的样本数量',
    task_status INT DEFAULT 0 COMMENT '任务状态：0-待采集，1-采集中，2-已完成，3-已失败',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '完成时间',
    operator VARCHAR(50) COMMENT '操作员',
    task_type INT DEFAULT 1 COMMENT '任务类型：1-首次录入，2-更新人脸，3-补录人脸',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_capture_personnel FOREIGN KEY (personnel_id) REFERENCES personnel(id) ON DELETE CASCADE,
    INDEX idx_personnel_id (personnel_id),
    INDEX idx_task_status (task_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸采集任务表';

-- ============================================
-- 触发器：自动更新人员人脸数量
-- ============================================
DELIMITER //

CREATE TRIGGER tr_personnel_face_count_update
AFTER INSERT ON face_data
FOR EACH ROW
BEGIN
    IF NEW.deleted = 0 AND NEW.is_valid = 1 THEN
        UPDATE personnel
        SET face_data_count = (
            SELECT COUNT(*) FROM face_data
            WHERE personnel_id = NEW.personnel_id
              AND deleted = 0
              AND is_valid = 1
        ),
        face_registered = 1,
        update_time = NOW()
        WHERE id = NEW.personnel_id;
    END IF;
END//

CREATE TRIGGER tr_personnel_face_count_delete
AFTER UPDATE ON face_data
FOR EACH ROW
BEGIN
    IF OLD.deleted = 0 AND NEW.deleted = 1 THEN
        UPDATE personnel
        SET face_data_count = (
            SELECT COUNT(*) FROM face_data
            WHERE personnel_id = OLD.personnel_id
              AND deleted = 0
              AND is_valid = 1
        ),
        face_registered = CASE
            WHEN (SELECT COUNT(*) FROM face_data
                  WHERE personnel_id = OLD.personnel_id
                    AND deleted = 0
                    AND is_valid = 1) > 0 THEN 1
            ELSE 0
        END,
        update_time = NOW()
        WHERE id = OLD.personnel_id;
    END IF;
END//

DELIMITER ;

-- ============================================
-- 初始数据（使用 INSERT IGNORE 可重复执行）
-- ============================================

-- 部门数据
INSERT IGNORE INTO department (department_name, department_code, description, leader, phone, status) VALUES
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

-- 用户数据（管理员/考勤员）
INSERT IGNORE INTO sys_user (username, password, real_name, phone, email, role, status) VALUES
('admin', 'admin123', '系统管理员', '13900000001', 'admin@mine.com', 'admin', 1),
('admin2', 'admin123', '运维管理员', '13900000002', 'admin2@mine.com', 'admin', 1),
('attendance01', 'admin123', '考勤员-张丽', '13900000003', 'att01@mine.com', 'attendance', 1),
('attendance02', 'admin123', '考勤员-李娜', '13900000004', 'att02@mine.com', 'attendance', 1),
('attendance03', 'admin123', '考勤员-王芳', '13900000005', 'att03@mine.com', 'attendance', 1);

-- 考勤规则
INSERT IGNORE INTO attendance_rules (rule_name, work_start_time, work_end_time, late_threshold, early_leave_threshold, overtime_threshold, continuous_work_threshold, auto_calculate_work_days, auto_calculate_overtime, auto_alarm_exception, status) VALUES
('标准考勤规则', '08:00:00', '18:00:00', 10, 10, 4, 7, 1, 1, 1, 1),
('早班规则', '06:00:00', '14:00:00', 10, 10, 2, 7, 1, 1, 1, 1),
('中班规则', '14:00:00', '22:00:00', 10, 10, 2, 7, 1, 1, 1, 1),
('夜班规则', '22:00:00', '06:00:00', 10, 10, 4, 7, 1, 1, 1, 1);

-- ============================================
-- 数据验证
-- ============================================
SELECT '=== 数据统计 ===' AS info;
SELECT '部门数量' AS table_name, COUNT(*) AS count FROM department WHERE deleted = 0
UNION ALL SELECT '人员数量', COUNT(*) FROM personnel WHERE deleted = 0
UNION ALL SELECT '人脸数据', COUNT(*) FROM face_data WHERE deleted = 0
UNION ALL SELECT '用户数量', COUNT(*) FROM sys_user WHERE deleted = 0
UNION ALL SELECT '考勤记录', COUNT(*) FROM attendance_record WHERE deleted = 0
UNION ALL SELECT '考勤统计', COUNT(*) FROM attendance_statistics WHERE deleted = 0
UNION ALL SELECT '考勤规则', COUNT(*) FROM attendance_rules WHERE deleted = 0
UNION ALL SELECT '系统日志', COUNT(*) FROM sys_log WHERE deleted = 0
UNION ALL SELECT '人脸识别日志', COUNT(*) FROM face_recognition_log
UNION ALL SELECT '人脸采集任务', COUNT(*) FROM face_capture_task;

SELECT '=== 初始化完成 ===' AS message;