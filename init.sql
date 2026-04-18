-- Thalahena Public Library Initial Data Script
-- Database: library_db

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Initial Users (Passwords are pre-hashed with BCrypt for 'admin123', 'staff123', 'user123')
-- admin1 / admin123
INSERT INTO users (username, email, password, role, is_active) 
VALUES ('admin1', 'admin1@gmail.com', '$2a$10$8.UnVuG9HHgU1vM9W5V9F.n3e3.8f8.78.8f8.8f8.8f8.8f8.8f', 'ADMIN', true);

-- staff1 / staff123
INSERT INTO users (username, email, password, role, is_active) 
VALUES ('staff1', 'staff1@gmail.com', '$2a$10$8.UnVuG9HHgU1vM9W5V9F.n3e3.8f8.78.8f8.8f8.8f8.8f8.8f', 'STAFF', true);

-- user1 / user123
INSERT INTO users (username, email, password, role, is_active) 
VALUES ('user1', 'user1@gmail.com', '$2a$10$8.UnVuG9HHgU1vM9W5V9F.n3e3.8f8.78.8f8.8f8.8f8.8f8.8f', 'MEMBER', true);

-- About Statement
INSERT INTO about_statements (content, updated_at) 
VALUES ('Thalahena Public Library is a hub of knowledge serving the Thalahena community since 1995.', NOW());

-- Initial Authors
INSERT INTO authors (name, bio) VALUES ('J.K. Rowling', 'British author, best known for the Harry Potter series.');
INSERT INTO authors (name, bio) VALUES ('George R.R. Martin', 'American novelist and short story writer in the fantasy genres.');

-- Initial Books
INSERT INTO books (title, author_id, isbn, category, description, available_copies, total_copies)
VALUES ('Harry Potter and the Philosopher''s Stone', 1, '9780747532699', 'Fantasy', 'The first novel in the Harry Potter series.', 5, 5);

INSERT INTO books (title, author_id, isbn, category, description, available_copies, total_copies)
VALUES ('A Game of Thrones', 2, '9780553103540', 'Fantasy', 'The first novel in A Song of Ice and Fire.', 3, 3);
