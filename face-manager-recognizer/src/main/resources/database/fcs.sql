/*
Navicat MySQL Data Transfer

Source Server         : xlauncher
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : fcs

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-04-01 15:34:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bloc_hotel
-- ----------------------------
DROP TABLE IF EXISTS `bloc_hotel`;
CREATE TABLE `bloc_hotel` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `bloc_id` varchar(32) DEFAULT '0' COMMENT '集团编号',
  `bloc_name` varchar(255) DEFAULT '无' COMMENT '集团名称',
  `hotel_id` varchar(32) DEFAULT NULL COMMENT '酒店编号',
  `hotel_name` varchar(255) DEFAULT NULL COMMENT '酒店名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for face_predict
-- ----------------------------
DROP TABLE IF EXISTS `face_predict`;
CREATE TABLE `face_predict` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `predict_time` datetime DEFAULT NULL COMMENT '人脸预测识别时间',
  `predict_hotel` varchar(32) DEFAULT NULL COMMENT '酒店编号',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `user_age` int(10) DEFAULT NULL COMMENT '用户年龄',
  `user_sex` int(10) DEFAULT NULL COMMENT '用户性别，1是男性；0是女性',
  `user_card` varchar(50) DEFAULT NULL COMMENT '用户身份证号码',
  `is_shelter` int(10) DEFAULT '0' COMMENT '是否遮挡，1是遮挡；0不是遮挡',
  `is_abnormal` int(10) DEFAULT '0' COMMENT '是否异常，1是异常；0不是异常',
  `predict_image` mediumblob COMMENT '人脸预测识别抓取的图片数据',
  `face_photo` mediumblob COMMENT '摄像头抓拍人脸图片',
  `model_calculation` varchar(8192) DEFAULT NULL COMMENT '人脸识别分析模型计算结果',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2160 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for syn_user
-- ----------------------------
DROP TABLE IF EXISTS `syn_user`;
CREATE TABLE `syn_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '运营云同步用户姓名',
  `user_age` int(10) DEFAULT NULL COMMENT '运营云同步用户年龄',
  `user_sex` int(10) DEFAULT NULL COMMENT '运营云同步用户性别',
  `user_card` varchar(50) DEFAULT NULL COMMENT '运营云同步用户身份证号码',
  `checkin_time` datetime DEFAULT NULL COMMENT '用户入住时间',
  `checkout_time` datetime DEFAULT NULL COMMENT '用户离店时间',
  `user_image` mediumblob COMMENT '用户身份证图片',
  `user_hotel` varchar(32) DEFAULT NULL COMMENT '运管云同步用户入住酒店编号',
  `model_calculation` varchar(8192) DEFAULT NULL COMMENT '人脸识别分析模型计算结果，',
  `model_version` int(10) DEFAULT NULL COMMENT '人脸识别分析模型版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=560 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `account` varchar(25) DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `token` varchar(255) DEFAULT NULL COMMENT 'token'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
