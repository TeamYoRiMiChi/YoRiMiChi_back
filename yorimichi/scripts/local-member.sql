-- Run only against your local MySQL. Existing member rows are preserved.
-- Existing legacy MEMBER tables already have password; do not add cognito_sub.
CREATE DATABASE IF NOT EXISTS yorimichi_db CHARACTER SET utf8mb4;
USE yorimichi_db;
CREATE TABLE IF NOT EXISTS MEMBER (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    personal_customs_code VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    withdrawn_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- Other application tables (orders, addresses, products, etc.) use the project schema.
