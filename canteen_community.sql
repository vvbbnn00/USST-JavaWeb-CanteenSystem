/*
 @Author: Lu Tiancheng, Xu Weizhe

 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3306
 Source Schema         : canteen_community

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 02/12/2023 16:38:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `announcement_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `canteen_id` int NOT NULL COMMENT '食堂ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`announcement_id`) USING BTREE,
  INDEX `idx_announcement_canteen_id`(`canteen_id` ASC) USING BTREE,
  INDEX `idx_announcement_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_announcement_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`canteen_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `announcement_chk_1` CHECK ((length(`content`) > 0) and (length(`content`) < 5000))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for canteen
-- ----------------------------
DROP TABLE IF EXISTS `canteen`;
CREATE TABLE `canteen`  (
  `canteen_id` int NOT NULL AUTO_INCREMENT COMMENT '食堂ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食堂名称',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '食堂位置',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '食堂简介',
  `comp_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '综合评分',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`canteen_id`) USING BTREE,
  INDEX `idx_canteen_name`(`name` ASC) USING BTREE,
  INDEX `idx_canteen_comp_score`(`comp_score` ASC) USING BTREE,
  INDEX `idx_canteen_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_canteen_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `canteen_chk_1` CHECK ((`comp_score` >= 0) and (`comp_score` <= 5))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '食堂表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `type` enum('canteen','item','complaint','topic') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论类型',
  `reference_id` int NOT NULL COMMENT '评论对象ID',
  `created_by` int NULL DEFAULT NULL COMMENT '评论者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论内容',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '评分',
  `parent_id` int NULL DEFAULT NULL COMMENT '父评论ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_comment_type_reference_id`(`type` ASC, `reference_id` ASC) USING BTREE,
  INDEX `idx_comment_created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_comment_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_comment_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_comment_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_chk_1` CHECK ((length(`content`) > 0) and (length(`content`) < 500)),
  CONSTRAINT `comment_chk_2` CHECK ((`score` >= 0) and (`score` <= 5))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表,包括食堂评论、菜品评论、帖子评论和投诉评论（投诉评论不能回复，以列表形式呈现）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for complaint
-- ----------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint`  (
  `complaint_id` int NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `created_by` int NULL DEFAULT NULL COMMENT '投诉者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '投诉内容',
  `status` enum('pending','processing','replied','finished') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '投诉状态, pending: 待处理, processing: 处理中, replied: 已回复, finished: 已完成',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`complaint_id`) USING BTREE,
  INDEX `created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_complaint_status_created_by`(`status` ASC, `created_by` ASC) USING BTREE,
  INDEX `idx_complaint_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_complaint_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `complaint_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `complaint_chk_1` CHECK ((length(`content`) > 0) and (length(`content`) < 5000))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投诉表,当用户发起投诉，状态为pending，管理员回复后状态为replied，用户回复后状态为processing，管理员处理完成后状态为finished，投诉信息不可被删除' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cuisine
-- ----------------------------
DROP TABLE IF EXISTS `cuisine`;
CREATE TABLE `cuisine`  (
  `cuisine_id` int NOT NULL AUTO_INCREMENT COMMENT '菜系ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜系名称',
  `canteen_id` int NULL DEFAULT NULL COMMENT '食堂ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`cuisine_id`) USING BTREE,
  INDEX `idx_cuisine_name`(`name` ASC) USING BTREE,
  INDEX `idx_cuisine_canteen_id`(`canteen_id` ASC) USING BTREE,
  INDEX `idx_cuisine_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_cuisine_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `cuisine_ibfk_1` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`canteen_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for image
-- ----------------------------
DROP TABLE IF EXISTS `image`;
CREATE TABLE `image`  (
  `image_id` int NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件ID',
  `type` enum('canteen','item','complaint','topic') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片类型',
  `reference_id` int NULL DEFAULT NULL COMMENT '图片对象ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`image_id`) USING BTREE,
  INDEX `idx_image_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_image_type_reference_id`(`type` ASC, `reference_id` ASC) USING BTREE,
  INDEX `idx_image_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_image_updated_at`(`updated_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `item_id` int NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜品名称',
  `cuisine_id` int NULL DEFAULT NULL COMMENT '菜系ID',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '菜品价格',
  `promotion_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '促销价格',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '菜品简介',
  `comp_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '综合评分',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `idx_item_name`(`name` ASC) USING BTREE,
  INDEX `idx_item_cuisine_id`(`cuisine_id` ASC) USING BTREE,
  INDEX `idx_item_price_promotion_price`(`price` ASC, `promotion_price` ASC) USING BTREE,
  INDEX `idx_item_comp_score`(`comp_score` ASC) USING BTREE,
  INDEX `idx_item_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_item_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`cuisine_id`) REFERENCES `cuisine` (`cuisine_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `item_chk_1` CHECK ((`comp_score` >= 0) and (`comp_score` <= 5))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`  (
  `topic_id` int NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `created_by` int NULL DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`topic_id`) USING BTREE,
  INDEX `idx_topic_created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_topic_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_topic_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `topic_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `topic_chk_1` CHECK ((length(`content`) > 0) and (length(`content`) < 5000))
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic_like
-- ----------------------------
DROP TABLE IF EXISTS `topic_like`;
CREATE TABLE `topic_like`  (
  `topic_id` int NOT NULL COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`topic_id`, `user_id`) USING BTREE,
  INDEX `idx_topic_like_topic_id`(`topic_id` ASC) USING BTREE,
  INDEX `idx_topic_like_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_topic_like_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_topic_like_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `topic_like_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`topic_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `topic_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码,使用Bcrypt加密,长度60,预留20',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `employee_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学工号',
  `level` int NULL DEFAULT 0 COMMENT '用户等级',
  `point` bigint NULL DEFAULT 0 COMMENT '积分',
  `available` tinyint NULL DEFAULT 1 COMMENT '是否可用',
  `role` enum('user','canteen_admin','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '用户角色',
  `is_verified` tinyint NULL DEFAULT 0 COMMENT '是否已验证学工号',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_at` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `level`(`level` ASC) USING BTREE,
  INDEX `is_verified`(`is_verified` ASC) USING BTREE,
  INDEX `idx_user_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_level`(`level` ASC) USING BTREE,
  INDEX `idx_user_is_verified`(`is_verified` ASC) USING BTREE,
  INDEX `idx_user_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_user_updated_at`(`updated_at` ASC) USING BTREE,
  INDEX `idx_user_last_login_at`(`last_login_at` ASC) USING BTREE,
  INDEX `idx_user_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `from_user_id` int NULL DEFAULT NULL COMMENT '发送者ID',
  `to_user_id` int NULL DEFAULT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `from_user_id`(`from_user_id` ASC) USING BTREE,
  INDEX `to_user_id`(`to_user_id` ASC) USING BTREE,
  CONSTRAINT `user_message_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_message_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_message_chk_1` CHECK ((length(`content`) > 0) and (length(`content`) < 200))
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification
-- ----------------------------
DROP TABLE IF EXISTS `user_notification`;
CREATE TABLE `user_notification`  (
  `notification_id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_point_log
-- ----------------------------
DROP TABLE IF EXISTS `user_point_log`;
CREATE TABLE `user_point_log`  (
  `log_id` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `point` int NULL DEFAULT NULL COMMENT '积分',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '积分详情',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_user_point_log_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_point_log_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_user_point_log_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `user_point_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户积分日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vote
-- ----------------------------
DROP TABLE IF EXISTS `vote`;
CREATE TABLE `vote`  (
  `vote_id` int NOT NULL AUTO_INCREMENT COMMENT '投票ID',
  `vote_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投票名称',
  `starttime` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `endtime` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `vote_intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '投票简介',
  `if_more` tinyint NULL DEFAULT 0 COMMENT '是否多选',
  `max` int NULL DEFAULT NULL COMMENT '最多选择数',
  `min` int NULL DEFAULT 1 COMMENT '最少选择数',
  `created_by` int NULL DEFAULT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`vote_id`) USING BTREE,
  INDEX `idx_vote_created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_vote_starttime_endtime`(`starttime` ASC, `endtime` ASC) USING BTREE,
  INDEX `idx_vote_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_vote_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `vote_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `vote_chk_1` CHECK ((length(`vote_intro`) > 0) and (length(`vote_intro`) < 500)),
  CONSTRAINT `vote_chk_2` CHECK (`max` > 0),
  CONSTRAINT `vote_chk_3` CHECK (`min` > 0)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投票表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for vote_option
-- ----------------------------
DROP TABLE IF EXISTS `vote_option`;
CREATE TABLE `vote_option`  (
  `vote_option_id` int NOT NULL AUTO_INCREMENT COMMENT '投票选项ID',
  `vote_id` int NULL DEFAULT NULL COMMENT '投票ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项名称',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`vote_option_id`) USING BTREE,
  INDEX `idx_vote_option_vote_id`(`vote_id` ASC) USING BTREE,
  INDEX `idx_vote_option_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_vote_option_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `vote_option_ibfk_1` FOREIGN KEY (`vote_id`) REFERENCES `vote` (`vote_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投票选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for voter
-- ----------------------------
DROP TABLE IF EXISTS `voter`;
CREATE TABLE `voter`  (
  `voter_id` int NOT NULL AUTO_INCREMENT COMMENT '投票者ID',
  `vote_id` int NOT NULL COMMENT '投票ID',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `option_id` int NULL DEFAULT NULL COMMENT '选项ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`voter_id`) USING BTREE,
  UNIQUE INDEX `vote_id`(`vote_id` ASC, `user_id` ASC, `option_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `option_id`(`option_id` ASC) USING BTREE,
  INDEX `idx_voter_vote_id_user_id_option_id`(`vote_id` ASC, `user_id` ASC, `option_id` ASC) USING BTREE,
  INDEX `idx_voter_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_voter_updated_at`(`updated_at` ASC) USING BTREE,
  CONSTRAINT `voter_ibfk_1` FOREIGN KEY (`vote_id`) REFERENCES `vote` (`vote_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `voter_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `voter_ibfk_3` FOREIGN KEY (`option_id`) REFERENCES `vote_option` (`vote_option_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投票者记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Triggers structure for table canteen
-- ----------------------------
DROP TRIGGER IF EXISTS `canteen_after_delete`;
delimiter ;;
CREATE TRIGGER `canteen_after_delete` AFTER DELETE ON `canteen` FOR EACH ROW BEGIN
DELETE FROM
    `comment`
WHERE
    `type` = 'canteen'
    AND `reference_id` = OLD.`canteen_id`;

END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table complaint
-- ----------------------------
DROP TRIGGER IF EXISTS `complaint_after_delete`;
delimiter ;;
CREATE TRIGGER `complaint_after_delete` AFTER DELETE ON `complaint` FOR EACH ROW BEGIN
DELETE FROM
    `comment`
WHERE
    `type` = 'complaint'
    AND `reference_id` = OLD.`complaint_id`;

DELETE FROM
    `image`
WHERE
    `type` = 'complaint'
    AND `reference_id` = OLD.`complaint_id`;

END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table item
-- ----------------------------
DROP TRIGGER IF EXISTS `item_after_delete`;
delimiter ;;
CREATE TRIGGER `item_after_delete` AFTER DELETE ON `item` FOR EACH ROW BEGIN
DELETE FROM
    `comment`
WHERE
    `type` = 'item'
    AND `reference_id` = OLD.`item_id`;

DELETE FROM
    `image`
WHERE
    `type` = 'item'
    AND `reference_id` = OLD.`item_id`;

END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table topic
-- ----------------------------
DROP TRIGGER IF EXISTS `topic_after_delete`;
delimiter ;;
CREATE TRIGGER `topic_after_delete` AFTER DELETE ON `topic` FOR EACH ROW BEGIN
DELETE FROM
    `comment`
WHERE
    `type` = 'topic'
    AND `reference_id` = OLD.`topic_id`;

DELETE FROM
    `image`
WHERE
    `type` = 'topic'
    AND `reference_id` = OLD.`topic_id`;

END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
