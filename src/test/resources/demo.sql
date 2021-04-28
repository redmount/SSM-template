/*
 Navicat Premium Data Transfer

 Source Server         : redmount.cn
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 192.144.231.168:3306
 Source Schema         : ssm

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 28/04/2021 12:51:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for r_test_teacher_t_test_clazz
-- ----------------------------
DROP TABLE IF EXISTS `r_test_teacher_t_test_clazz`;
CREATE TABLE `r_test_teacher_t_test_clazz`  (
  `pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `teacher_pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `clazz_pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `course` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `count` int(0) NULL DEFAULT NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `updated` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_test_teacher_t_test_clazz
-- ----------------------------
INSERT INTO `r_test_teacher_t_test_clazz` VALUES ('1', 't2', 'c1', '体育老师教的语文课', 1, '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `r_test_teacher_t_test_clazz` VALUES ('2', 't2', 'c2', '体育老师教的英语课', 0, '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `r_test_teacher_t_test_clazz` VALUES ('3', 't3', 'c2', '数学课', 210, '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `r_test_teacher_t_test_clazz` VALUES ('4', 't4', 'c2', '英语课', 310, '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `r_test_teacher_t_test_clazz` VALUES ('5', 't1', 'c1', '语文课', 310, '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `r_test_teacher_t_test_clazz` VALUES ('6', 't3', 'c1', '数学课', 210, '2018-12-30 03:07:41', '2018-12-30 03:07:41');

-- ----------------------------
-- Table structure for sys_service_exception
-- ----------------------------
DROP TABLE IF EXISTS `sys_service_exception`;
CREATE TABLE `sys_service_exception`  (
  `pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `code` int(0) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常标题',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常信息主体',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '造成异常的原因',
  `suggest` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建议操作',
  `created` datetime(0) NULL DEFAULT NULL,
  `updated` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '业务异常表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_service_exception
-- ----------------------------
INSERT INTO `sys_service_exception` VALUES ('100001', 100001, '登录失败', '用户名不存在', '您输入的用户名没有找到', '请重新输入用户名', '2018-12-30 03:07:41', '2018-12-30 03:07:41');

-- ----------------------------
-- Table structure for test_clazz
-- ----------------------------
DROP TABLE IF EXISTS `test_clazz`;
CREATE TABLE `test_clazz`  (
  `pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级名称',
  `adviser_pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班主任pk',
  `updated` datetime(0) NULL DEFAULT NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_clazz
-- ----------------------------
INSERT INTO `test_clazz` VALUES ('c1', '班级1', 't1', '2018-12-30 03:07:41', '2018-12-25 15:03:47', '一年一班', NULL);
INSERT INTO `test_clazz` VALUES ('c2', '班级2', 't4', '2018-12-25 15:03:43', '2018-12-25 15:03:47', '二年二班', NULL);
INSERT INTO `test_clazz` VALUES ('c3', '班级3', '', '2018-12-24 10:21:05', '2018-12-24 10:20:37', '三年一班', NULL);

-- ----------------------------
-- Table structure for test_clazz_info
-- ----------------------------
DROP TABLE IF EXISTS `test_clazz_info`;
CREATE TABLE `test_clazz_info`  (
  `pk` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `clazz_pk` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `introduction` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `updated` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`pk`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班级信息扩展表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_clazz_info
-- ----------------------------
INSERT INTO `test_clazz_info` VALUES ('class_info_1', 'c1', '一班的介绍', NULL, '2018-12-30 03:07:41', '2018-12-30 03:07:41');

-- ----------------------------
-- Table structure for test_student
-- ----------------------------
DROP TABLE IF EXISTS `test_student`;
CREATE TABLE `test_student`  (
  `pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学生名称',
  `clazz_pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属班级pk',
  `updated` datetime(0) NULL DEFAULT NULL,
  `created` datetime(0) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_student
-- ----------------------------
INSERT INTO `test_student` VALUES ('s1', '一班学生1', 'c1', '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `test_student` VALUES ('s2', '一班学生2', 'c1', '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `test_student` VALUES ('s01', '二班学生1', 'c2', '2018-12-30 03:07:41', '2018-12-30 03:07:41');

-- ----------------------------
-- Table structure for test_teacher
-- ----------------------------
DROP TABLE IF EXISTS `test_teacher`;
CREATE TABLE `test_teacher`  (
  `pk` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教师名称',
  `created` datetime(0) NULL DEFAULT NULL,
  `updated` datetime(0) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教师表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_teacher
-- ----------------------------
INSERT INTO `test_teacher` VALUES ('t1', '一班班主任', '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `test_teacher` VALUES ('t2', '语文老师', '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `test_teacher` VALUES ('t3', '数学老师', '2018-12-30 03:07:41', '2018-12-30 03:07:41');
INSERT INTO `test_teacher` VALUES ('t4', '二班班主任', '2018-12-30 03:07:41', '2018-12-30 03:07:41');

SET FOREIGN_KEY_CHECKS = 1;
