# 二手书交易系统（旧书斋）

Spring Boot + Vue3 全栈二手书电商平台。

## 项目结构

```
books-shop-api/   — Spring Boot 后端（REST API）
books-vue/        — Vue3 前端（Element Plus）
建议.md           — 企业级升级方案
```

## 快速开始

```bash
# 1. 初始化数据库
mysql -u root -p < books-shop-api/src/main/resources/schema.sql

# 2. 修改 application.yml 中的数据库密码

# 3. 启动后端
cd books-shop-api
mvn spring-boot:run

# 4. 启动前端
cd books-vue
npm install
npm run dev
```

后端：`http://localhost:8080/api`

前端：`http://localhost:5173`

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 用户 | user1 | 123456 |

## 技术栈

**后端**：Spring Boot 2.7 · MyBatis-Plus · Spring Security · JWT · Redis · RabbitMQ · Elasticsearch · WebSocket

**前端**：Vue3 · Element Plus · Vue Router · Pinia · Axios · SockJS/STOMP

**数据库与中间件**：MySQL 8 · Redis · RabbitMQ · Elasticsearch
