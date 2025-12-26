-- =========================
-- Online Shopping DB Schema (MySQL 8+)
-- =========================

CREATE DATABASE IF NOT EXISTS shopdb
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE shopdb;

-- -------------------------
-- 1) 用户 / 管理员
-- -------------------------
CREATE TABLE users (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  username        VARCHAR(50)  NOT NULL,
  email           VARCHAR(100) NULL,
  phone           VARCHAR(20)  NULL,
  password_hash   VARCHAR(255) NOT NULL,        -- bcrypt/argon2 hash
  role            ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  status          ENUM('ACTIVE','DISABLED') NOT NULL DEFAULT 'ACTIVE',
  last_login_at   DATETIME NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_phone (phone)
) ENGINE=InnoDB;

-- 可选：登录会话（如果你不用 JWT，而用 session/token 表）
CREATE TABLE user_sessions (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id       BIGINT NOT NULL,
  session_token VARCHAR(255) NOT NULL,
  expires_at    DATETIME NOT NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_session_token (session_token),
  KEY idx_sessions_user_id (user_id),
  CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------
-- 2) 商品分类 / 商品
-- -------------------------
CREATE TABLE categories (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id   BIGINT NULL,
  name        VARCHAR(100) NOT NULL,
  sort_order  INT NOT NULL DEFAULT 0,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_categories_name_parent (name, parent_id),
  KEY idx_categories_parent (parent_id),
  CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE products (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id   BIGINT NOT NULL,
  sku           VARCHAR(64) NOT NULL,
  name          VARCHAR(200) NOT NULL,
  description   TEXT NULL,
  price         DECIMAL(10,2) NOT NULL,
  stock         INT NOT NULL DEFAULT 0,
  status        ENUM('ON_SALE','OFF_SALE') NOT NULL DEFAULT 'ON_SALE',
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_products_sku (sku),
  KEY idx_products_category (category_id),
  KEY idx_products_status (status),
  FULLTEXT KEY ft_products_name_desc (name, description),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 可选：商品图片
CREATE TABLE product_images (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id  BIGINT NOT NULL,
  image_url   VARCHAR(500) NOT NULL,
  is_primary  TINYINT(1) NOT NULL DEFAULT 0,
  sort_order  INT NOT NULL DEFAULT 0,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_images_product (product_id),
  CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------
-- 3) 购物车
-- -------------------------
CREATE TABLE carts (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_carts_user (user_id),
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cart_items (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id     BIGINT NOT NULL,
  product_id  BIGINT NOT NULL,
  quantity    INT NOT NULL,
  checked     TINYINT(1) NOT NULL DEFAULT 1, -- 是否勾选结算
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_cart_product (cart_id, product_id),
  KEY idx_cart_items_cart (cart_id),
  KEY idx_cart_items_product (product_id),
  CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------
-- 4) 订单 / 支付
-- -------------------------
CREATE TABLE orders (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no         VARCHAR(32) NOT NULL,   -- 业务订单号
  user_id          BIGINT NOT NULL,
  status           ENUM('PENDING_PAY','PAID','SHIPPED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING_PAY',
  total_amount     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  pay_amount       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  currency         CHAR(3) NOT NULL DEFAULT 'CNY',

  -- 收货信息（简化版：也可拆成 address 表）
  receiver_name    VARCHAR(50) NOT NULL,
  receiver_phone   VARCHAR(20) NOT NULL,
  receiver_address VARCHAR(500) NOT NULL,

  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  paid_at          DATETIME NULL,
  shipped_at       DATETIME NULL,
  completed_at     DATETIME NULL,
  cancelled_at     DATETIME NULL,

  UNIQUE KEY uk_orders_order_no (order_no),
  KEY idx_orders_user (user_id),
  KEY idx_orders_status_time (status, created_at),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE order_items (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id      BIGINT NOT NULL,
  product_id    BIGINT NOT NULL,

  -- 下单快照：避免商品改名/改价影响历史订单
  product_name  VARCHAR(200) NOT NULL,
  unit_price    DECIMAL(10,2) NOT NULL,
  quantity      INT NOT NULL,
  subtotal      DECIMAL(10,2) NOT NULL,

  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_order_items_order (order_id),
  KEY idx_order_items_product (product_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE payments (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id      BIGINT NOT NULL,
  pay_channel   ENUM('BANK','ALIPAY','WECHAT','MOCK') NOT NULL DEFAULT 'MOCK',
  pay_status    ENUM('INIT','SUCCESS','FAILED') NOT NULL DEFAULT 'INIT',
  amount        DECIMAL(10,2) NOT NULL,
  bank_txn_no   VARCHAR(64) NULL,          -- 银行/支付平台交易号（回调提供）
  card_holder   VARCHAR(80) NULL,
  card_last4    CHAR(4) NULL,              -- 只存后4位演示
  requested_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  paid_at       DATETIME NULL,
  raw_payload   JSON NULL,                 -- 可选：保存回调/请求报文（演示用）
  KEY idx_payments_order (order_id),
  KEY idx_payments_status (pay_status),
  CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -------------------------
-- 5) 报表：销售统计（视图 + 常用查询）
-- -------------------------

-- 商品销售汇总（销量/销售额）
CREATE OR REPLACE VIEW v_product_sales AS
SELECT
  p.id AS product_id,
  p.name AS product_name,
  SUM(oi.quantity) AS total_qty,
  SUM(oi.subtotal) AS total_amount
FROM order_items oi
JOIN orders o ON o.id = oi.order_id
JOIN products p ON p.id = oi.product_id
WHERE o.status IN ('PAID','SHIPPED','COMPLETED')
GROUP BY p.id, p.name;

-- 分类销售汇总
CREATE OR REPLACE VIEW v_category_sales AS
SELECT
  c.id AS category_id,
  c.name AS category_name,
  SUM(oi.quantity) AS total_qty,
  SUM(oi.subtotal) AS total_amount
FROM order_items oi
JOIN orders o ON o.id = oi.order_id
JOIN products p ON p.id = oi.product_id
JOIN categories c ON c.id = p.category_id
WHERE o.status IN ('PAID','SHIPPED','COMPLETED')
GROUP BY c.id, c.name;

-- 每日销售额（可做趋势图）
CREATE OR REPLACE VIEW v_daily_sales AS
SELECT
  DATE(o.paid_at) AS paid_date,
  COUNT(*) AS order_count,
  SUM(o.pay_amount) AS total_amount
FROM orders o
WHERE o.status IN ('PAID','SHIPPED','COMPLETED') AND o.paid_at IS NOT NULL
GROUP BY DATE(o.paid_at)
ORDER BY paid_date;

-- -------------------------
-- 6) 一些推荐索引（检索/报表性能）
-- -------------------------
-- 如果你关键词检索不是 FULLTEXT，而是 LIKE，可加 name 索引；但 FULLTEXT 已提供
-- 订单查询常用 user_id + created_at
CREATE INDEX idx_orders_user_time ON orders(user_id, created_at);

