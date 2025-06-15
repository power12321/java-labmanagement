CREATE DATABASE `lab_management`;
USE `lab_management`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for sys_choice
-- ----------------------------
DROP TABLE IF EXISTS `sys_choice`;
CREATE TABLE `sys_choice`  (
  `choice_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '选课ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `score` int(11) NULL DEFAULT NULL COMMENT '课程打分',
  `evaluate` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程评价',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `upload_resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`choice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生选课表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_course
-- ----------------------------
DROP TABLE IF EXISTS `sys_course`;
CREATE TABLE `sys_course`  (
  `course_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实验课程ID',
  `number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验课程编号',
  `course_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验课程名称',
  `college` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程所属学院实验',
  `major` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验课程所属专业',
  `period` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课时',
  `credits` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学分',
  `type` int(11) NULL DEFAULT 0 COMMENT '实验课程类型 0-必修 1-选修',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_device
-- ----------------------------
DROP TABLE IF EXISTS `sys_device`;
CREATE TABLE `sys_device`  (
  `device_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实验设备ID',
  `teacher_id` bigint(20) NULL DEFAULT NULL COMMENT '借用设备的教师ID',
  `device_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验设备编号',
  `device_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验设备名称',
  `category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实验设备分类',
  `status` int(11) NOT NULL COMMENT '设备状态 0-待借用 1-借用中 2-申请借用中',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '实验设备表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(11) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

INSERT INTO `sys_dict_data` VALUES (22, 5, '计算机科学与工程学院', '6', 'college', '0', '2025-06-01 10:30:17', NULL, '学院');
INSERT INTO `sys_dict_data` VALUES (23, 6, '机械与汽车工程学院', '7', 'college', '0', '2025-06-01 10:30:17', NULL, '学院');

INSERT INTO `sys_dict_data` VALUES
                                (24, 0, '计算机科学与技术', '11', 'major', '0', '2025-06-01 10:30:17', NULL, '22'),
                                (25, 1, '信息安全', '12', 'major', '0', '2025-06-01 10:30:17', NULL, '22'),
                                (26, 2, '网络工程', '13', 'major', '0', '2025-06-01 10:30:17', NULL, '22'),
                                (27, 3, '计算机科学与技术双学位', '14', 'major', '0', '2025-06-01 10:30:17', NULL, '22');
INSERT INTO `sys_dict_data` VALUES
                                (28, 0, '机械工程', '15', 'major', '0', '2025-06-01 10:30:17', NULL, '23'),
                                (29, 1, '机械电子工程', '16', 'major', '0', '2025-06-01 10:30:17', NULL, '23'),
                                (30, 2, '车辆工程', '17', 'major', '0', '2025-06-01 10:30:17', NULL, '23');
-- ----------------------------
-- Table structure for sys_schedule
-- ----------------------------
DROP TABLE IF EXISTS `sys_schedule`;
CREATE TABLE `sys_schedule`  (
  `schedule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '排课ID',
  `user_id` bigint(20) NOT NULL COMMENT '教师ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '课程开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '课程结束时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `upload_resource` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '实验课程指导书',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '实验概述',
  PRIMARY KEY (`schedule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '排课表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_student`;
CREATE TABLE `sys_student`  (
  `student_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '学生详情ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `college` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学院',
  `major` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业',
  `grade` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`student_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_submit
-- ----------------------------
DROP TABLE IF EXISTS `sys_submit`;
CREATE TABLE `sys_submit`  (
  `user_id` bigint(20) NOT NULL,
  `course_id` bigint(20) NOT NULL,
  `submit_resource` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID 0-管理员 1-教师 2-学生',
  `account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号 学号/工号',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `sex` int(11) NULL DEFAULT NULL COMMENT '性别（1男 2女）',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `review_status` int(11) NULL DEFAULT 0 COMMENT '审核状态 0-未审核 1-审核未通过 2-审核通过 3-无需审核',
  `status` int(11) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '是否逻辑删除 0否 1是',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (8, 0, 'admin', '管理员', 1, '18102039378', '7X50HmU7ELk/k0d5o1RJNA==', 3, 0, 0, '2025-06-01 09:47:35', '2025-06-01 09:47:38', '管理员');


SET FOREIGN_KEY_CHECKS = 1;
