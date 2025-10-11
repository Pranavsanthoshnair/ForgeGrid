-- =====================================================
-- ForgeGrid MySQL Database Migration Script
-- =====================================================
-- This script creates the MySQL database and tables
-- Run this in phpMyAdmin → SQL tab → paste and click "Go"
-- =====================================================

-- Create database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS forgegrid 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE forgegrid;

-- =====================================================
-- Create users table (converted from SQLite)
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    onboarding_completed TINYINT(1) DEFAULT 0,
    onboarding_goal VARCHAR(255) NULL,
    onboarding_language VARCHAR(255) NULL,
    onboarding_skill VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Create indexes for better performance
-- =====================================================
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_onboarding_completed ON users(onboarding_completed);

-- =====================================================
-- Insert sample data (optional - remove if not needed)
-- =====================================================
-- INSERT INTO users (username, email, password, onboarding_completed, onboarding_goal, onboarding_language, onboarding_skill) 
-- VALUES 
-- ('admin', 'admin@forgegrid.com', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 1, 'Learn programming fundamentals', 'Java', 'Beginner (just starting out)'),
-- ('testuser', 'test@forgegrid.com', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 0, NULL, NULL, NULL);

-- =====================================================
-- Verify table creation
-- =====================================================
SHOW TABLES;
DESCRIBE users;

-- =====================================================
-- Success message
-- =====================================================
SELECT 'ForgeGrid MySQL database setup completed successfully!' AS status;
