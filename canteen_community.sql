/*
Navicat MySQL Data Transfer

Source Server         : experiment
Source Server Version : 80034
Source Host           : localhost:3306
Source Database       : canteen_community

Target Server Type    : MYSQL
Target Server Version : 80034
File Encoding         : 65001

Date: 2023-12-02 11:41:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for canteen
-- ----------------------------
DROP TABLE IF EXISTS `canteen`;
CREATE TABLE `canteen` (
  `canteen_id` int NOT NULL AUTO_INCREMENT,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `introduction` varbinary(255) DEFAULT NULL,
  `notice` binary(255) DEFAULT NULL,
  `menu` binary(255) DEFAULT NULL,
  PRIMARY KEY (`canteen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of canteen
-- ----------------------------

-- ----------------------------
-- Table structure for canteen_comment
-- ----------------------------
DROP TABLE IF EXISTS `canteen_comment`;
CREATE TABLE `canteen_comment` (
  `canteen_comment_id` int NOT NULL,
  `canteen_id` int DEFAULT NULL COMMENT '所评价的canteen_id',
  `created_by` int DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `content` binary(255) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`canteen_comment_id`),
  KEY `cc_canteen_id` (`canteen_id`),
  KEY `cc_created_by` (`created_by`),
  CONSTRAINT `cc_canteen_id` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`canteen_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cc_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of canteen_comment
-- ----------------------------

-- ----------------------------
-- Table structure for canteen_rank
-- ----------------------------
DROP TABLE IF EXISTS `canteen_rank`;
CREATE TABLE `canteen_rank` (
  `canteen_id` int NOT NULL AUTO_INCREMENT,
  `introduction` binary(255) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  `position` int DEFAULT NULL,
  PRIMARY KEY (`canteen_id`),
  CONSTRAINT `cr_canteen_id` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`canteen_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of canteen_rank
-- ----------------------------

-- ----------------------------
-- Table structure for complaint
-- ----------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint` (
  `complaint_id` int NOT NULL AUTO_INCREMENT,
  `created_by` int DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `content` binary(255) DEFAULT NULL,
  PRIMARY KEY (`complaint_id`),
  KEY `complaint_created_by` (`created_by`),
  CONSTRAINT `complaint_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of complaint
-- ----------------------------

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `cuisine` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜系',
  `price` decimal(10,2) DEFAULT NULL,
  `canteen_id` int DEFAULT NULL COMMENT '所在食堂',
  `introduction` binary(255) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL COMMENT '总评分',
  PRIMARY KEY (`item_id`),
  KEY `item_canteen_id` (`canteen_id`),
  CONSTRAINT `item_canteen_id` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`canteen_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of item
-- ----------------------------

-- ----------------------------
-- Table structure for item_comment
-- ----------------------------
DROP TABLE IF EXISTS `item_comment`;
CREATE TABLE `item_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `item_id` int DEFAULT NULL COMMENT '所评价的canteen_id',
  `created_by` int DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `content` binary(255) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `ic_item_id` (`item_id`),
  KEY `ic_created_by` (`created_by`),
  CONSTRAINT `ic_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ic_item_id` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of item_comment
-- ----------------------------

-- ----------------------------
-- Table structure for item_rank
-- ----------------------------
DROP TABLE IF EXISTS `item_rank`;
CREATE TABLE `item_rank` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `introduction` binary(255) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  `position` int DEFAULT NULL,
  PRIMARY KEY (`item_id`),
  CONSTRAINT `ir_item_id` FOREIGN KEY (`item_id`) REFERENCES `item` (`item_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of item_rank
-- ----------------------------

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL COMMENT '促销活动，推荐菜品，或其他',
  `time` datetime DEFAULT NULL COMMENT '发布时间',
  `created_by` varchar(255) DEFAULT NULL COMMENT '发布者',
  `content` binary(255) DEFAULT NULL,
  PRIMARY KEY (`notice_id`),
  KEY `notice_created_by` (`created_by`),
  CONSTRAINT `notice_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of notice
-- ----------------------------

-- ----------------------------
-- Table structure for ponit_log
-- ----------------------------
DROP TABLE IF EXISTS `ponit_log`;
CREATE TABLE `ponit_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `point_change` decimal(5,2) DEFAULT NULL,
  `level_change` int DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `pl_user_id` (`user_id`),
  CONSTRAINT `pl_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of ponit_log
-- ----------------------------

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `topic_id` int NOT NULL AUTO_INCREMENT,
  `type` smallint NOT NULL,
  `title` binary(255) DEFAULT NULL,
  `content` binary(255) DEFAULT NULL,
  `point` int DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  PRIMARY KEY (`topic_id`),
  KEY `topic_created_by` (`created_by`),
  CONSTRAINT `topic_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of topic
-- ----------------------------

-- ----------------------------
-- Table structure for topic_comment
-- ----------------------------
DROP TABLE IF EXISTS `topic_comment`;
CREATE TABLE `topic_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `topic_id` int DEFAULT NULL COMMENT '所属topic_id',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `created_by` int NOT NULL,
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `log_user_id` (`created_by`) USING BTREE,
  KEY `tc_topic_id` (`topic_id`),
  CONSTRAINT `tc_crated_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tc_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of topic_comment
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `employee_id` varchar(50) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL COMMENT '三种用户角色：系统管理员、师生用户、食堂管理员',
  `level` bigint DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL COMMENT '是否被屏蔽',
  PRIMARY KEY (`user_id`),
  KEY `user_id` (`user_id`,`username`),
  KEY `employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------

-- ----------------------------
-- Table structure for vote
-- ----------------------------
DROP TABLE IF EXISTS `vote`;
CREATE TABLE `vote` (
  `vote_id` int NOT NULL,
  `vote_name` varchar(255) DEFAULT NULL,
  `starttime` datetime DEFAULT NULL,
  `endtime` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `vote_intro` varchar(255) DEFAULT NULL,
  `votetime` datetime DEFAULT NULL,
  `if_more` smallint DEFAULT NULL COMMENT '确定是否为多选',
  `max` int DEFAULT NULL,
  `min` int DEFAULT '1',
  `created_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`vote_id`),
  KEY `vote_created_by` (`created_by`),
  CONSTRAINT `vote_created_by` FOREIGN KEY (`created_by`) REFERENCES `user` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of vote
-- ----------------------------

-- ----------------------------
-- Table structure for voter
-- ----------------------------
DROP TABLE IF EXISTS `voter`;
CREATE TABLE `voter` (
  `voter_id` int NOT NULL,
  `vote_id` int NOT NULL COMMENT '所属的vote_id',
  `user_id` int DEFAULT NULL COMMENT '投票人：用户名、IP地址、未知',
  `option_id` int DEFAULT NULL COMMENT '所投的选项',
  PRIMARY KEY (`vote_id`,`voter_id`),
  KEY `voter_user_id` (`user_id`),
  KEY `voter_option_id` (`option_id`),
  CONSTRAINT `voter_option_id` FOREIGN KEY (`option_id`) REFERENCES `vote_option` (`vote_option_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `voter_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `voter_vote_id` FOREIGN KEY (`vote_id`) REFERENCES `vote` (`vote_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of voter
-- ----------------------------

-- ----------------------------
-- Table structure for vote_option
-- ----------------------------
DROP TABLE IF EXISTS `vote_option`;
CREATE TABLE `vote_option` (
  `vote_option_id` int NOT NULL COMMENT '主键',
  `vote_id` int DEFAULT NULL COMMENT '所属vote_id',
  `name` varchar(100) DEFAULT NULL COMMENT '选项名称',
  `img_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `number` int DEFAULT NULL COMMENT '被投数量',
  `update_by` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(255) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`vote_option_id`),
  KEY `vo_vote_id` (`vote_id`),
  CONSTRAINT `vo_vote_id` FOREIGN KEY (`vote_id`) REFERENCES `vote` (`vote_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of vote_option
-- ----------------------------
