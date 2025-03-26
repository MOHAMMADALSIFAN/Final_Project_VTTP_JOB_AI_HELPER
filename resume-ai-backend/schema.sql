DROP DATABASE IF EXISTS finalproject;
CREATE DATABASE finalproject;
USE finalproject;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    oauth_id VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    picture VARCHAR(1024),
    provider VARCHAR(50),
    created_at DATETIME,
    last_login DATETIME,
    password VARCHAR(255)
);

-- Create table for storing Telegram tokens with foreign key
CREATE TABLE telegram_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(oauth_id) ON DELETE CASCADE
);

-- Create table for storing Telegram chat IDs with foreign key
CREATE TABLE telegram_chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    chat_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(oauth_id) ON DELETE CASCADE
);

-- Create table for storing resumes with user reference
CREATE TABLE resumes (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    user_input TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_created_at (created_at),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(oauth_id) ON DELETE CASCADE
);

-- Create table for storing resume PDFs with foreign key
CREATE TABLE resume_pdfs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    pdf_data MEDIUMBLOB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(oauth_id) ON DELETE CASCADE
);

-- Change the foreign key to allow NULL values for temporary tokens
ALTER TABLE telegram_tokens DROP FOREIGN KEY telegram_tokens_ibfk_1;
ALTER TABLE telegram_tokens MODIFY user_id VARCHAR(255) NULL;
ALTER TABLE telegram_tokens ADD CONSTRAINT telegram_tokens_ibfk_1 
    FOREIGN KEY (user_id) REFERENCES users(oauth_id) ON DELETE CASCADE;