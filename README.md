# Online Shopping System

一个基于 Spring Boot 3 + MySQL 的在线购物系统，前端使用 Vue 3 + Vite。支持商品浏览、购物车、下单、模拟支付、订单管理，以及管理员商品与报表管理。

## 技术栈
- 后端：Spring Boot 3、Spring Security、JPA、JWT、MySQL 8
- 前端：Vue 3、Vite、Pinia、Vue Router、Element Plus、Axios

## 功能概览
- 用户注册/登录（JWT）
- 商品与分类浏览、搜索、详情
- 购物车增删改查
- 下单与订单管理
- 模拟支付（不接第三方）
- 管理后台：商品管理、订单查询、报表

## 目录结构
- `backend/`：后端服务
- `frontend/`：前端项目
- `docs/`：运行指南与 API 文档
- `mysql struct.md`：数据库结构

## 快速开始
### 1) 数据库
先执行 `mysql struct.md` 中的建表 SQL，数据库名：`shopdb`。

### 2) 运行后端
```bash
cd backend
mvn spring-boot:run
```
默认端口：`http://localhost:8080`

### 3) 运行前端
```bash
cd frontend
npm install
npm run dev
```
默认端口：`http://localhost:5173`

## 配置
后端默认使用环境变量配置数据库与 JWT：
- `DB_URL`（默认：`jdbc:mysql://localhost:3306/shopdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai`）
- `DB_USERNAME`（默认：`root`）
- `DB_PASSWORD`（默认：`root`）
- `JWT_SECRET`（默认：`dev-secret-change-me-dev-secret-change-me`）
- `JWT_EXPIRE_MINUTES`（默认：`120`）

## 管理员入口
- 管理后台路由：`/admin/products`、`/admin/orders`、`/admin/reports`
- 仅 `ADMIN` 角色可访问

## 初始化数据
系统启动时如果 `products` 表为空，会自动写入一些演示商品与图片（仅首次）。

## 文档
- `docs/run-backend.md`
- `docs/run-frontend.md`
- `docs/api-backend.md`

