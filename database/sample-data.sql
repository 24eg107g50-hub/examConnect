-- ExamConnect Database Standalone Setup & Sample Data
-- This file can be imported directly into MySQL:
-- mysql -u root -p examconnect < sample-data.sql

CREATE DATABASE IF NOT EXISTS examconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE examconnect;

-- Flyway migrations V1 and V2 automatically handle the schema and seed data when the Spring Boot application starts.
-- Refer to backend/src/main/resources/db/migration/ for granular migration scripts.
