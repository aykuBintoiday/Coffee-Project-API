CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  phone VARCHAR(20),
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  slug VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(160) NOT NULL,
  slug VARCHAR(200) NOT NULL UNIQUE,
  price DECIMAL(12,2) NOT NULL,
  image_url VARCHAR(500),
  description TEXT,
  status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  category_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  code VARCHAR(32) NOT NULL UNIQUE,
  total DECIMAL(12,2) NOT NULL,
  status ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  receiver_name VARCHAR(120),
  receiver_phone VARCHAR(20),
  receiver_address VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_orders_user ON orders(user_id);

-- Seed
INSERT INTO categories(name, slug) VALUES ('Cà phê hạt','ca-phe-hat'),('Đồ uống','do-uong');
INSERT INTO products(name, slug, price, image_url, description, category_id)
VALUES ('Cà phê Arabica', 'ca-phe-arabica', 120000, NULL, 'Hạt Arabica rang vừa', 1),
       ('Latte', 'latte', 45000, NULL, 'Ly latte ấm', 2);
