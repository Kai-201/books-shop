DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS order_info;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS book_category;
DROP TABLE IF EXISTS user_info;
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    login_name  VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    staff_no    VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_info (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    login_name  VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    username    VARCHAR(50)  NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book_category (
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE book (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    book_sn     VARCHAR(20)    NOT NULL,
    book_name   VARCHAR(100)   NOT NULL,
    book_author VARCHAR(50)    DEFAULT '未知作者',
    category_id INT,
    book_price  DECIMAL(10, 2) NOT NULL,
    books_num   INT            NOT NULL DEFAULT 0,
    book_cover  VARCHAR(255),
    uploader_id INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    user_id  INT NOT NULL,
    book_id  INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    UNIQUE (user_id, book_id)
);

CREATE TABLE order_info (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    order_no     VARCHAR(32)    NOT NULL UNIQUE,
    user_id      INT            NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status       TINYINT        NOT NULL DEFAULT 0,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
    id        INT PRIMARY KEY AUTO_INCREMENT,
    order_id  INT            NOT NULL,
    book_id   INT            NOT NULL,
    book_name VARCHAR(100)   NOT NULL,
    price     DECIMAL(10, 2) NOT NULL,
    quantity  INT            NOT NULL
);

INSERT INTO admin (login_name, password, staff_no) VALUES ('admin', '123456', '001');

INSERT INTO user_info (login_name, password, username, phone, email) VALUES
('user1', '123456', '张三', '13800000001', 'user1@test.com'),
('user2', '123456', '李四', '13800000002', 'user2@test.com');

INSERT INTO book_category (name) VALUES
('文学'), ('计算机'), ('数学'), ('物理'), ('教育');

INSERT INTO book (book_sn, book_name, book_author, category_id, book_price, books_num) VALUES
('BK001', 'Java 编程思想', 'Bruce Eckel', 2, 89.00, 10),
('BK002', '深入理解计算机系统', 'Randal Bryant', 2, 128.00, 5),
('BK003', '平凡的世界', '路遥', 1, 45.00, 20);
