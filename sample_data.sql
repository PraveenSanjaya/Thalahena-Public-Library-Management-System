-- ============================================
-- Thalahena Public Library - Sample Data
-- ============================================
-- This script populates the database with test data
-- Run this after the application has created the tables
-- ============================================

USE library_db;

-- ============================================
-- 1. USERS (10 examples - Mix of ADMIN, STAFF, MEMBER)
-- ============================================

-- Admin Users (2)
INSERT INTO users (username, password, email, role, first_name, last_name, birth_date, gender, phone, whatsapp, is_active, membership_date) VALUES
('admin1', '$2a$10$zT7D85KocL1zped7B4HA2euXCbwUaIZ99v.E5fFffrsT4tmoTlNJm', 'admin1@gmail.com', 'ADMIN', 'Admin', 'User', '1985-03-15', 'MALE', '+94771111111', '+94771111111', 1, '2020-01-01'),
('admin2', '$2a$10$zT7D85KocL1zped7B4HA2euXCbwUaIZ99v.E5fFffrsT4tmoTlNJm', 'admin2@gmail.com', 'ADMIN', 'Sarah', 'Johnson', '1990-07-22', 'FEMALE', '+94772222222', '+94772222222', 1, '2021-03-10');

-- Staff Users (3)
INSERT INTO users (username, password, email, role, first_name, last_name, birth_date, gender, phone, whatsapp, is_active, membership_date) VALUES
('staff1', '$2a$10$6aPSvN5wM9rzT9vAgz0kBeAfALNWJ5wNPT/.oHdOiQETbrmxptNRG', 'staff1@gmail.com', 'STAFF', 'Staff', 'User', '1988-11-30', 'MALE', '+94773333333', '+94773333333', 1, '2021-06-15'),
('staff2', '$2a$10$6aPSvN5wM9rzT9vAgz0kBeAfALNWJ5wNPT/.oHdOiQETbrmxptNRG', 'staff2@gmail.com', 'STAFF', 'Emily', 'Davis', '1992-05-18', 'FEMALE', '+94774444444', '+94774444444', 1, '2022-01-20'),
('staff3', '$2a$10$6aPSvN5wM9rzT9vAgz0kBeAfALNWJ5wNPT/.oHdOiQETbrmxptNRG', 'staff3@gmail.com', 'STAFF', 'Michael', 'Brown', '1987-09-25', 'MALE', '+94775555555', '+94775555555', 1, '2022-08-05');

-- Member Users (5)
INSERT INTO users (username, password, email, role, first_name, last_name, birth_date, gender, phone, whatsapp, social_media, is_active, membership_date) VALUES
('user1', '$2a$10$qMpWd/cnAdjMstr08NppMOMAWhOFNF.ecReOFmfDf3gr4Az.2UdAG', 'user1@gmail.com', 'MEMBER', 'Member', 'User', '2005-04-14', 'MALE', '+94776666666', '+94776666666', '@user1_fb', 1, '2023-01-10'),
('john_doe', '$2a$10$qMpWd/cnAdjMstr08NppMOMAWhOFNF.ecReOFmfDf3gr4Az.2UdAG', 'john.doe@gmail.com', 'MEMBER', 'John', 'Doe', '1995-08-20', 'MALE', '+94777777777', '+94777777777', '@johndoe', 1, '2023-03-15'),
('jane_smith', '$2a$10$qMpWd/cnAdjMstr08NppMOMAWhOFNF.ecReOFmfDf3gr4Az.2UdAG', 'jane.smith@gmail.com', 'MEMBER', 'Jane', 'Smith', '1998-12-05', 'FEMALE', '+94778888888', '+94778888888', '@janesmith', 1, '2023-05-20'),
('alex_wilson', '$2a$10$qMpWd/cnAdjMstr08NppMOMAWhOFNF.ecReOFmfDf3gr4Az.2UdAG', 'alex.wilson@gmail.com', 'MEMBER', 'Alex', 'Wilson', '2008-02-28', 'MALE', '+94779999999', '+94779999999', '@alexw', 1, '2023-09-01'),
('maria_garcia', '$2a$10$qMpWd/cnAdjMstr08NppMOMAWhOFNF.ecReOFmfDf3gr4Az.2UdAG', 'maria.garcia@gmail.com', 'MEMBER', 'Maria', 'Garcia', '2003-06-12', 'FEMALE', '+94770000000', '+94770000000', '@mariag', 1, '2024-01-15');

-- ============================================
-- 2. AUTHORS (10 examples)
-- ============================================

INSERT INTO authors (name, biography, nationality, birth_date) VALUES
('F. Scott Fitzgerald', 'American novelist and short story writer, widely regarded as one of the greatest American writers of the 20th century.', 'American', '1896-09-24'),
('Harper Lee', 'American novelist best known for her Pulitzer Prize-winning novel To Kill a Mockingbird.', 'American', '1926-04-28'),
('George Orwell', 'English novelist, essayist, journalist, and critic, famous for dystopian novels.', 'British', '1903-06-25'),
('J.K. Rowling', 'British author best known for the Harry Potter fantasy series.', 'British', '1965-07-31'),
('Mark Twain', 'American writer, humorist, entrepreneur, publisher, and lecturer.', 'American', '1835-11-30'),
('Jane Austen', 'English novelist known primarily for her six major novels of romantic fiction.', 'British', '1775-12-16'),
('Charles Dickens', 'English writer and social critic, creator of some of the world\'s best-known fictional characters.', 'British', '1812-02-07'),
('Gabriel García Márquez', 'Colombian novelist, short-story writer, screenwriter, and journalist, known for magical realism.', 'Colombian', '1927-03-06'),
('Leo Tolstoy', 'Russian writer regarded as one of the greatest authors of all time.', 'Russian', '1828-09-09'),
('Agatha Christie', 'English writer known for her sixty-six detective novels and fourteen short story collections.', 'British', '1890-09-15');

-- ============================================
-- 3. BOOKS (10 examples with different categories)
-- ============================================

INSERT INTO books (title, author, isbn, category, total_copies, available_copies, publication_year, description) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', '800 - Literature', 5, 3, 1925, 'A story of the mysteriously wealthy Jay Gatsby and his quixotic passion for Daisy Buchanan.'),
('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', '800 - Literature', 4, 2, 1960, 'A novel about racial injustice in the Depression-era South seen through the eyes of young Scout Finch.'),
('1984', 'George Orwell', '978-0451524935', '300 - Social Studies', 6, 4, 1949, 'A dystopian social science fiction novel and cautionary tale about the dangers of totalitarianism.'),
('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0590353427', '800 - Literature', 8, 5, 1997, 'The first novel in the Harry Potter series following a young wizard''s journey.'),
('The Adventures of Tom Sawyer', 'Mark Twain', '978-0143039563', '800 - Literature', 3, 2, 1876, 'A novel about a young boy growing up along the Mississippi River.'),
('Pride and Prejudice', 'Jane Austen', '978-0141439518', '800 - Literature', 4, 3, 1813, 'A romantic novel of manners following Elizabeth Bennet as she deals with issues of morality and marriage.'),
('A Tale of Two Cities', 'Charles Dickens', '978-0141439600', '800 - Literature', 3, 1, 1859, 'A historical novel set in London and Paris before and during the French Revolution.'),
('One Hundred Years of Solitude', 'Gabriel García Márquez', '978-0060883287', '800 - Literature', 2, 1, 1967, 'A landmark novel that tells the multi-generational story of the Buendía family.'),
('War and Peace', 'Leo Tolstoy', '978-0199232765', '900 - History', 2, 0, 1869, 'A literary masterpiece chronicling the French invasion of Russia and its impact on Tsarist society.'),
('Murder on the Orient Express', 'Agatha Christie', '978-0062693662', '800 - Literature', 5, 4, 1934, 'A classic detective novel featuring the famous Belgian detective Hercule Poirot.');

-- ============================================
-- 4. TRANSACTIONS (10 examples - Book borrowings)
-- ============================================

-- Issued books (not yet returned)
INSERT INTO transactions (user_id, book_id, issue_date, due_date, status, fine_amount) VALUES
(3, 1, '2024-04-01', '2024-04-15', 'ISSUED', 0.00),  -- user1 borrowed The Great Gatsby
(6, 3, '2024-04-05', '2024-04-19', 'ISSUED', 0.00),  -- john_doe borrowed 1984
(7, 2, '2024-04-08', '2024-04-22', 'ISSUED', 0.00),  -- jane_smith borrowed To Kill a Mockingbird
(8, 4, '2024-04-10', '2024-04-24', 'ISSUED', 0.00);  -- alex_wilson borrowed Harry Potter

-- Returned books (with some fines)
INSERT INTO transactions (user_id, book_id, issue_date, due_date, return_date, status, fine_amount) VALUES
(3, 5, '2024-03-01', '2024-03-15', '2024-03-14', 'RETURNED', 0.00),  -- Returned on time
(6, 7, '2024-03-05', '2024-03-19', '2024-03-25', 'RETURNED', 30.00), -- 6 days late = Rs.30
(7, 6, '2024-03-10', '2024-03-24', '2024-03-24', 'RETURNED', 0.00),  -- Returned on time
(8, 8, '2024-03-12', '2024-03-26', '2024-04-02', 'RETURNED', 35.00), -- 7 days late = Rs.35
(9, 9, '2024-03-15', '2024-03-29', '2024-03-28', 'RETURNED', 0.00),  -- Returned early
(3, 10, '2024-03-20', '2024-04-03', '2024-04-10', 'RETURNED', 35.00); -- 7 days late = Rs.35

-- ============================================
-- 5. FINES (5 examples)
-- ============================================

INSERT INTO fines (transaction_id, amount, status, paid_date) VALUES
(6, 30.00, 'UNPAID', NULL),  -- john_doe owes Rs.30
(8, 35.00, 'PAID', '2024-04-03'),  -- alex_wilson paid Rs.35
(10, 35.00, 'UNPAID', NULL); -- user1 owes Rs.35

-- ============================================
-- 6. RESERVATIONS (5 examples)
-- ============================================

INSERT INTO reservations (user_id, book_id, reservation_date, status) VALUES
(3, 9, '2024-04-10', 'PENDING'),   -- user1 reserved War and Peace (currently unavailable)
(6, 1, '2024-04-11', 'PENDING'),   -- john_doe reserved The Great Gatsby
(7, 7, '2024-04-12', 'FULFILLED'), -- jane_smith's reservation fulfilled
(8, 2, '2024-04-13', 'PENDING'),   -- alex_wilson reserved To Kill a Mockingbird
(9, 4, '2024-04-14', 'CANCELLED'); -- maria_garcia cancelled Harry Potter reservation

-- ============================================
-- 7. FEEDBACK (5 examples)
-- ============================================

INSERT INTO feedback (user_id, message, created_at) VALUES
(3, 'Great library with an excellent collection of classic literature!', '2024-04-01 10:30:00'),
(6, 'The staff is very helpful and friendly. Love the new books section!', '2024-04-05 14:20:00'),
(7, 'Would appreciate more contemporary fiction books. Overall good experience.', '2024-04-08 09:15:00'),
(8, 'The library is quiet and perfect for studying. Thank you!', '2024-04-10 16:45:00'),
(9, 'Excellent service! The online reservation system is very convenient.', '2024-04-12 11:30:00');

-- ============================================
-- 8. ABOUT STATEMENTS (5 examples)
-- ============================================

INSERT INTO about (content, updated_at) VALUES
('Welcome to Thalahena Public Library - Your gateway to knowledge and imagination!', '2024-01-01 00:00:00'),
('Library Hours: Monday to Saturday, 9:00 AM - 6:00 PM. Closed on Sundays and Poya days.', '2024-01-15 00:00:00'),
('New members can register with a valid ID and proof of address. Annual membership fee: Rs. 500.', '2024-02-01 00:00:00'),
('Books can be borrowed for 14 days. Renewals available if no one has reserved the book.', '2024-02-15 00:00:00'),
('Late returns incur a fine of Rs. 5 per day. Please return books on time to avoid penalties.', '2024-03-01 00:00:00');

-- ============================================
-- 9. NOTIFICATIONS (5 examples)
-- ============================================

INSERT INTO notifications (user_id, message, type, is_read, created_at) VALUES
(3, 'Your book "The Great Gatsby" is due on April 15, 2024. Please return on time.', 'DUE_DATE', 0, '2024-04-10 09:00:00'),
(6, 'Your reserved book "The Great Gatsby" is now available for pickup!', 'RESERVATION', 0, '2024-04-11 10:30:00'),
(7, 'Your book "To Kill a Mockingbird" was successfully returned. Thank you!', 'GENERAL', 1, '2024-04-08 15:00:00'),
(8, 'Reminder: You have an overdue book. Please return it as soon as possible.', 'OVERDUE', 0, '2024-04-12 08:00:00'),
(3, 'Your feedback has been received. Thank you for your valuable input!', 'GENERAL', 1, '2024-04-01 11:00:00');

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Check data counts
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Authors', COUNT(*) FROM authors
UNION ALL
SELECT 'Books', COUNT(*) FROM books
UNION ALL
SELECT 'Transactions', COUNT(*) FROM transactions
UNION ALL
SELECT 'Fines', COUNT(*) FROM fines
UNION ALL
SELECT 'Reservations', COUNT(*) FROM reservations
UNION ALL
SELECT 'Feedback', COUNT(*) FROM feedback
UNION ALL
SELECT 'About Statements', COUNT(*) FROM about
UNION ALL
SELECT 'Notifications', COUNT(*) FROM notifications;

-- Check user distribution by role
SELECT role, COUNT(*) as count FROM users GROUP BY role;

-- Check book categories
SELECT category, COUNT(*) as count FROM books GROUP BY category;

-- Check transaction status
SELECT status, COUNT(*) as count FROM transactions GROUP BY status;

-- Check fine status
SELECT status, COUNT(*) as count, SUM(amount) as total_amount FROM fines GROUP BY status;
