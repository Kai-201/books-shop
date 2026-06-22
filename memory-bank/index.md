# 二手书交易系统 — Memory Bank 入口

## 项目概述

Spring Boot 2.7 + Vue3 全栈二手书电商平台。用户浏览/发布图书、购物车下单、模拟支付、订单管理；管理员管理图书/用户/订单、数据统计。

## 模块地图

| 模块 | 文件 | 核心功能 |
|------|------|---------|
| 认证与用户 | [01-认证与用户.md](business/01-认证与用户.md) | 登录注册、JWT、Spring Security、用户管理 |
| 图书与分类 | [02-图书与分类.md](business/02-图书与分类.md) | 图书CRUD、分类管理、ES搜索、缓存 |
| 购物车 | [03-购物车.md](business/03-购物车.md) | 添加/修改/清空、库存校验 |
| 订单与支付 | [04-订单与支付.md](business/04-订单与支付.md) | 下单、支付、取消、库存扣减、RabbitMQ、WebSocket |

## 架构速览

详见 [architecture.md](architecture.md)

```
前端 Vue3 → Axios → http://localhost:8080/api
                      │
                      ▼
              Spring Security + JWT
                      │
                      ▼
              Controller 层 (7个)
                      │
                      ▼
              Service 层 (6个)
                      │
          ┌───────┬───┴───┬───────┬──────┐
          ▼       ▼       ▼       ▼      ▼
       MySQL   Redis   RabbitMQ   ES   WebSocket
```

## 数据库速览

详见 [database.md](database.md)

7 张表：`user_info`、`admin`、`book`、`book_category`、`cart`、`order_info`、`order_item`
