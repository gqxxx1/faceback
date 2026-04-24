-- ============================================
-- 矿井人员考勤系统数据库初始化脚本（含人脸识别）
-- 请在 MySQL 中执行: source D:/桌面/FaceAttendanceSystem/database.sql;
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
-- 2. 人员档案表（修改版）
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
-- 3. 人脸数据表（核心表 - 存储OpenCV处理后的人脸数据）
-- ============================================
DROP TABLE IF EXISTS face_data;
CREATE TABLE face_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人脸数据ID',
    personnel_id BIGINT NOT NULL COMMENT '关联人员ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '工号（冗余字段，便于查询）',
    face_image MEDIUMBLOB COMMENT '灰度处理后的人脸图像数据（OpenCV处理后的灰度图）',
    face_image_path VARCHAR(255) COMMENT '人脸图片存储路径（备选，存储原始图或处理后图）',
    face_feature LONGTEXT COMMENT '人脸特征向量（JSON格式，存储OpenCV/LBPH特征）',
    face_descriptor VARCHAR(8000) COMMENT '人脸描述符（128维浮点数序列，用于人脸比对）',
    image_width INT COMMENT '图像宽度（像素）',
    image_height INT COMMENT '图像高度（像素）',
    capture_device VARCHAR(100) COMMENT '采集设备',
    capture_location VARCHAR(100) COMMENT '采集地点',
    capture_angle VARCHAR(50) COMMENT '采集角度（正面、左侧、右侧等）',
    image_quality_score DECIMAL(5,4) COMMENT '图像质量评分（0-1之间）',
    is_valid TINYINT(1) DEFAULT 1 COMMENT '是否有效：0-无效，1-有效',
    is_primary TINYINT(1) DEFAULT 0 COMMENT '是否为主图：0-否，1-是（用于识别时优先使用）',
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
    face_data_id BIGINT COMMENT '关联人脸数据ID（识别成功时记录）',
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
-- 9. 人脸识别日志表（新增 - 记录每次识别操作）
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
-- 10. 人脸采集任务表（新增 - 管理人脸采集流程）
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
-- 触发器：当personnel表人脸数量变化时更新face_registered字段
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
-- 验证数据
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
