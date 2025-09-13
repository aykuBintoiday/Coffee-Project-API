CREATE TABLE users (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  
  -- Thông tin định danh
  full_name       VARCHAR(120)    NOT NULL,
  email           VARCHAR(191)    NOT NULL,
  email_lc        VARCHAR(191)    GENERATED ALWAYS AS (LOWER(email)) STORED,
  
  -- Xác thực
  password_hash   VARCHAR(255)    NOT NULL,
  email_verified_at DATETIME NULL,
  
  -- Quản lý tài khoản
  role            ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  is_active       TINYINT(1)      NOT NULL DEFAULT 1,
  failed_attempts INT             NOT NULL DEFAULT 0,
  locked_until    DATETIME NULL,
  
  -- Nhật ký
  last_login_at   DATETIME NULL,
  created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email_lc (email_lc),
  KEY idx_users_role_active (role, is_active),
  KEY idx_users_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
