-- Use the library database
USE library_db;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE fines;
TRUNCATE TABLE reservations;
TRUNCATE TABLE transactions;
TRUNCATE TABLE books;
TRUNCATE TABLE authors;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- Synchronize database enums with Java entities
ALTER TABLE transactions MODIFY COLUMN book_condition ENUM('GOOD', 'POOR', 'DAMAGED');
ALTER TABLE reservations MODIFY COLUMN status ENUM('PENDING', 'AVAILABLE', 'UNAVAILABLE', 'COMPLETED', 'CANCELLED');
ALTER TABLE fines MODIFY COLUMN status ENUM('NONE', 'PAID', 'UNPAID');

-- Populate Authors
INSERT INTO authors (id, name, bio) VALUES
(1, 'J.K. Rowling', 'British author, best known for the Harry Potter fantasy series.'),
(2, 'George R.R. Martin', 'American novelist and short story writer in the fantasy, horror, and science fiction genres.'),
(3, 'Agatha Christie', 'English writer known for her 66 detective novels and 14 short story collections.'),
(4, 'Stephen King', 'American author of horror, supernatural fiction, suspense, crime, science-fiction, and fantasy novels.'),
(5, 'Haruki Murakami', 'Japanese writer. His novels, essays, and short stories have been bestsellers in Japan and internationally.');

-- Populate Books
INSERT INTO books (id, title, author_id, isbn, category, description, publisher, date_received, total_copies, available_copies) VALUES
(1, 'Harry Potter and the Sorcerer''s Stone', 1, '9780590353403', 'Fantasy', 'A young boy discovers he is a wizard and attends Hogwarts School of Witchcraft and Wizardry.', 'Scholastic', '2026-01-10', 5, 5),
(2, 'A Game of Thrones', 2, '9780553103540', 'Fantasy', 'Kings and queens, knights and renegades, liars, lords and honest men... all play the Game of Thrones.', 'Bantam Spectra', '2026-01-15', 3, 2),
(3, 'Murder on the Orient Express', 3, '9780007119318', 'Mystery', 'Detective Hercule Poirot investigates a murder on the famous Orient Express train.', 'Collins Crime Club', '2026-02-01', 4, 4),
(4, 'The Shining', 4, '9780385121675', 'Horror', 'Jack Torrance becomes the winter caretaker at the isolated Overlook Hotel.', 'Doubleday', '2026-02-10', 2, 1),
(5, 'Norwegian Wood', 5, '9784062036832', 'Romance', 'A story of loss and sexuality, centered on Toru Watanabe and his relationships.', 'Kodansha', '2026-03-01', 3, 3),
(6, 'Harry Potter and the Chamber of Secrets', 1, '9780439064866', 'Fantasy', 'Harry returns to Hogwarts for his second year, only to find the school in danger.', 'Scholastic', '2026-01-10', 5, 5),
(7, 'A Clash of Kings', 2, '9780553108033', 'Fantasy', 'The second book in A Song of Ice and Fire series.', 'Bantam Spectra', '2026-01-15', 3, 3),
(8, 'Death on the Nile', 3, '9780007119325', 'Mystery', 'A peaceful cruise along the Nile is shattered by a murder.', 'Collins Crime Club', '2026-02-01', 4, 3),
(9, 'It', 4, '9780670813025', 'Horror', 'A group of children are terrorized by an entity that takes the form of their fears.', 'Viking', '2026-02-10', 2, 2),
(10, 'Kafka on the Shore', 5, '9784103534228', 'Magical Realism', 'Two characters whose paths eventually intersect: Kafka Tamura and Satoru Nakata.', 'Kodansha', '2026-03-01', 3, 3);

-- Populate Users
-- Password for all is 'password' -> $2a$10$hKDVYxLefVhv/Ltu6zHiaOT.U1pP4W0Qy/XjD/XnLjyvH0X1hI4W.
INSERT INTO users (id, username, password, email, role, first_name, last_name, is_active, phone, membership_date) VALUES
(1, 'admin', '$2a$10$hKDVYxLefVhv/Ltu6zHiaOT.U1pP4W0Qy/XjD/XnLjyvH0X1hI4W.', 'admin@thalahena.lk', 'ADMIN', 'System', 'Administrator', 1, '0112345678', '2026-01-01'),
(2, 'staff1', '$2a$10$hKDVYxLefVhv/Ltu6zHiaOT.U1pP4W0Qy/XjD/XnLjyvH0X1hI4W.', 'staff1@thalahena.lk', 'STAFF', 'John', 'Doe', 1, '0771234567', '2026-01-05'),
(3, 'member1', '$2a$10$hKDVYxLefVhv/Ltu6zHiaOT.U1pP4W0Qy/XjD/XnLjyvH0X1hI4W.', 'member1@gmail.com', 'MEMBER', 'Alice', 'Silva', 1, '0714567890', '2026-02-15'),
(4, 'member2', '$2a$10$hKDVYxLefVhv/Ltu6zHiaOT.U1pP4W0Qy/XjD/XnLjyvH0X1hI4W.', 'member2@gmail.com', 'MEMBER', 'Bob', 'Perera', 1, '0751234567', '2026-02-20'),
(5, 'member3', '$2a$10$hKDVYxLefVhv/Ltu6zHiaOT.U1pP4W0Qy/XjD/XnLjyvH0X1hI4W.', 'member3@gmail.com', 'MEMBER', 'Charlie', 'Fernando', 1, '0729876543', '2026-03-05');

-- Populate Transactions
INSERT INTO transactions (id, user_id, book_id, issue_date, due_date, return_date, status, fine_amount, book_condition) VALUES
(1, 3, 2, '2026-04-01', '2026-04-15', '2026-04-14', 'RETURNED', 0.0, 'GOOD'),
(2, 4, 4, '2026-04-05', '2026-04-19', NULL, 'ISSUED', 0.0, NULL),
(3, 5, 8, '2026-04-10', '2026-04-24', '2026-04-12', 'RETURNED', 0.0, 'POOR');

-- Populate Reservations
INSERT INTO reservations (id, user_id, book_id, reservation_date, status, processed) VALUES
(1, 3, 1, '2026-04-16 10:00:00', 'AVAILABLE', 1),
(2, 4, 3, '2026-04-17 14:30:00', 'PENDING', 0),
(3, 5, 10, '2026-04-18 09:15:00', 'PENDING', 0);

-- Populate Fines
INSERT INTO fines (id, transaction_id, amount, status, return_date) VALUES
(1, 1, 0.0, 'NONE', '2026-04-14');
