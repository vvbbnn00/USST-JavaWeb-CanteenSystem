# JavaWeb-CanteenSystem
USST JavaWeb大作业：上海理工大学食堂点评交流社区

## 系统架构

前后端分离，2个前端 + 1个后端。

### 前端1：点评交流社区前端

适用普通用户，使用`react`开发。

### 前端2：管理员后台前端

适用于系统管理员和食堂管理员，使用`vue`开发，根据不同角色，渲染相应的管理功能。

### 后端

后端使用Java开发，使用分层架构。数据库使用`Redis`和`Mysql`。

## 目录结构

- `canteen_community.sql`：数据库表结构文件
- JavaBackend：Java后端项目目录
- CommunityFrontend: 社区前端
- AdminFrontend: 管理员后台前端
- nginx: Nginx配置文件
- mariadb: 数据库文件映射目录（保留目录）

## 开发环境

- 内网开发数据库：`192.168.19.2:3306`，用户名：`root`，密码：`password`
- 内网开发请使用`Wireguard`连接至开发环境。

## 部署方案

- 使用`Docker-Compose`部署，每一个子项目都应当有独立的`Dockerfile`。

## 文件存储服务

文件存储服务使用基于`lua`脚本编写的`openresty-file-server`，代码见：[openresty-file-server](https://github.com/vvbbnn00/openresty-file-server)
