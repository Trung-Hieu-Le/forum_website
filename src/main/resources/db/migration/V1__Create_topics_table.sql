-- Migration: Create topics table if not exists
-- This migration ensures the topics table exists before inserting data

CREATE TABLE IF NOT EXISTS topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

