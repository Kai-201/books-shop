# BooksShopManage

二手书交易系统 — Spring Boot REST 后端

## 项目说明

原 SSM + JSP 代码已移除，当前后端位于 `books-shop-api/` 目录。

## 快速开始

```bash
# 初始化数据库
mysql -u root -p < books-shop-api/src/main/resources/schema.sql

# 修改 books-shop-api/src/main/resources/application.yml 中的数据库密码

# 启动
cd books-shop-api
mvn spring-boot:run
```

服务地址：`http://localhost:8080/api`

详细接口文档见 [books-shop-api/README.md](books-shop-api/README.md)

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 用户 | user1 | 123456 |

## 技术栈

- Spring Boot 2.7
- MyBatis-Plus
- MySQL 8
- JWT 鉴权
