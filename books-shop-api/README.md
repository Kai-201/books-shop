# 二手书交易系统 - Spring Boot 后端

基于 Spring Boot 2.7 + MyBatis-Plus 的 REST API 后端，对应课程设计需求，可直接对接 Vue 前端。

## 技术栈

- Spring Boot 2.7.18
- MyBatis-Plus 3.5
- MySQL 8
- JWT 鉴权

## 功能模块

| 模块 | 功能 |
|------|------|
| 认证 | 管理员登录、用户登录、用户注册 |
| 用户管理 | 管理员查询/新增/修改/删除用户 |
| 图书管理 | 图书 CRUD、分类筛选、用户上传自己的书 |
| 分类管理 | 分类列表、新增、删除 |
| 购物车 | 添加、改数量、删除、清空、总价计算 |
| 订单管理 | 下单、我的订单、管理员查看/改状态/删除、统计 |

## 图书字段（精简版）

保留核心字段，已去除归属地、出版社编号、出版日期等：

| 字段 | 说明 |
|------|------|
| bookSn | 图书编号 |
| bookName | 书名 |
| bookAuthor | 作者 |
| categoryId | 分类 ID |
| bookPrice | 价格 |
| booksNum | 库存 |
| bookCover | 封面 URL（可选） |
| uploaderId | 上传者 ID |

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 2. 修改数据库配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/books_shop?...
    username: root
    password: 你的密码
```

### 3. 启动项目

```bash
cd books-shop-api
mvn spring-boot:run
```

服务地址：`http://localhost:8080/api`

### 4. 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 用户 | user1 | 123456 |

## API 接口

统一响应格式：

```json
{ "code": 200, "message": "success", "data": {} }
```

请求头（除登录/公开查询外）：

```
Authorization: Bearer <token>
```

### 认证 `/auth`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/admin/login` | 管理员登录 |
| POST | `/auth/user/login` | 用户登录 |
| POST | `/auth/user/register` | 用户注册 |

### 用户 `/users`（管理员）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/users/me` | 当前用户信息 |
| GET | `/users` | 用户列表 |
| GET | `/users/page?page=1&size=10` | 分页 |
| POST | `/users` | 新增用户 |
| PUT | `/users/{id}` | 修改用户 |
| DELETE | `/users/{id}` | 删除用户 |

### 图书 `/books`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/books` | 图书列表（公开） |
| GET | `/books/page` | 分页查询（公开） |
| GET | `/books/my` | 我上传的书（用户） |
| GET | `/books/{id}` | 详情（公开） |
| POST | `/books` | 新增 |
| PUT | `/books/{id}` | 修改 |
| DELETE | `/books/{id}` | 删除 |

### 分类 `/categories`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/categories` | 分类列表（公开） |
| POST | `/categories` | 新增（管理员） |
| DELETE | `/categories/{id}` | 删除（管理员） |

### 购物车 `/cart`（用户）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/cart` | 购物车及总价 |
| POST | `/cart` | 添加 `{bookId, quantity}` |
| PUT | `/cart/{id}` | 改数量 `{quantity}` |
| DELETE | `/cart/{id}` | 删除单项 |
| DELETE | `/cart` | 清空 |

### 订单 `/orders`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/orders` | 从购物车下单（用户） |
| GET | `/orders/my` | 我的订单（用户） |
| GET | `/orders/{id}` | 订单详情 |
| GET | `/orders` | 订单分页（管理员） |
| PUT | `/orders/{id}/status` | 改状态 `{status}` |
| DELETE | `/orders/{id}` | 删除（管理员） |
| GET | `/orders/statistics` | 订单统计（管理员） |

订单状态：`0` 待付款 · `1` 已付款 · `2` 已完成 · `3` 已取消

## Vue 前端对接示例

```javascript
import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8080/api'
})

// 登录后保存 token
const login = async () => {
  const { data } = await request.post('/auth/user/login', {
    loginName: 'user1',
    password: '123456'
  })
  localStorage.setItem('token', data.data.token)
}

// 带 token 请求
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
```

## 项目结构

```
books-shop-api/
├── src/main/java/com/bookshop/
│   ├── controller/     # REST 接口
│   ├── service/        # 业务逻辑
│   ├── mapper/         # MyBatis-Plus
│   ├── entity/         # 实体
│   ├── dto/            # 请求/响应对象
│   ├── config/         # 配置
│   ├── interceptor/    # JWT 拦截器
│   └── common/         # 统一响应、异常
└── src/main/resources/
    ├── application.yml
    └── schema.sql      # 建表脚本
```
