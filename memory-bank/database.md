# 数据库设计

数据库名：`books_shop`，MySQL 8（InnoDB），字符集 utf-8

## 表结构

### user_info — 普通用户
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| login_name | VARCHAR UNIQUE | 登录账号 |
| password | VARCHAR | BCrypt 加密 |
| username | VARCHAR | 昵称 |
| phone | VARCHAR | 手机（注册时非必填） |
| email | VARCHAR | 邮箱（注册时非必填） |
| create_time | DATETIME | |

### admin — 管理员
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| login_name | VARCHAR UNIQUE | |
| password | VARCHAR | BCrypt |
| staff_no | VARCHAR | |

### book — 图书
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| book_sn | VARCHAR | 编号 |
| book_name | VARCHAR | 书名（有 idx_book_name 索引） |
| book_author | VARCHAR | 作者 |
| category_id | INT | 分类 ID（有 idx_category 索引） |
| book_price | DECIMAL | 价格 |
| books_num | INT | 库存数量 |
| book_cover | VARCHAR | 封面图 URL |
| uploader_id | INT NULL | NULL=管理员上架，有值=用户发布（有 idx_uploader 索引） |
| version | INT | **乐观锁版本号**（@Version） |
| create_time | DATETIME | |

### book_category — 分类
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| name | VARCHAR UNIQUE | |

### cart — 购物车
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| user_id | INT | 联合唯一索引 uk_user_book(user_id, book_id) |
| book_id | INT | 防止重复加入 |
| quantity | INT | |

### order_info — 订单
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| order_no | VARCHAR UNIQUE | 订单号 |
| user_id | INT | 有 idx_user 索引 |
| total_amount | DECIMAL | 总金额 |
| status | INT | 0=待付款 1=已付款 2=已完成 3=已取消（有 idx_status 索引） |
| create_time | DATETIME | |

### order_item — 订单明细
| 列 | 类型 | 说明 |
|----|------|------|
| id | INT PK AUTO | |
| order_id | INT | 有 idx_order 索引 |
| book_id | INT | |
| book_name | VARCHAR | 冗余存书名（防书被删后找不到） |
| price | DECIMAL | 下单时的单价 |
| quantity | INT | |
