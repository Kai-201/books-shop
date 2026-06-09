# Postman 接口测试指南

## 1. 启动后端

```bash
# 确保 MySQL 已初始化
mysql -u root -p < src/main/resources/schema.sql

# 启动服务
mvn spring-boot:run
```

服务地址：`http://localhost:8080/api`

## 2. 导入 Postman 集合

1. 打开 Postman
2. 点击 **Import** → 选择 `postman/BooksShopAPI.postman_collection.json`
3. 导入后在左侧看到「二手书交易系统 API」集合

## 3. 测试顺序（推荐）

| 步骤 | 接口 | 说明 |
|------|------|------|
| 1 | 01-认证 → 用户登录 | 自动保存 `userToken` |
| 2 | 01-认证 → 管理员登录 | 自动保存 `adminToken` |
| 3 | 02-图书 → 图书列表 | 无需 Token |
| 4 | 03-购物车 → 加入购物车 | 需要 userToken |
| 5 | 03-购物车 → 查看购物车 | 确认商品和总价 |
| 6 | 04-订单 → 下单 | 从购物车生成订单 |
| 7 | 04-订单 → 我的订单 | 查看订单历史 |
| 8 | 05-用户管理 → 用户列表 | 需要 adminToken |
| 9 | 04-订单 → 管理员-订单统计 | 需要 adminToken |

## 4. Token 说明

登录成功后，集合变量会自动更新：
- `userToken` — 用户接口用
- `adminToken` — 管理员接口用

手动查看：集合 → Variables 标签页。

请求头格式：
```
Authorization: Bearer {{userToken}}
```

## 5. 默认测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | 123456 |
| 用户 | user1 | 123456 |

## 6. 运行 JUnit 自动化测试

```bash
mvn test
```

使用内存 H2 数据库，无需 MySQL 即可跑通全部用例。
