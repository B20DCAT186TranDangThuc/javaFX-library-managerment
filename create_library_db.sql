-- Tạo database nếu chưa có
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

-- 1. Bảng tác giả
CREATE TABLE authors (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL
);

-- 2. Bảng sách
CREATE TABLE books (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author_id INT,
                       publisher VARCHAR(255),
                       year_published INT,
                       total_quantity INT DEFAULT 1,
                       available_quantity INT DEFAULT 1,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id)
                           ON DELETE SET NULL ON UPDATE CASCADE
);

-- 3. Bảng người dùng (người mượn sách)
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255),
                       phone VARCHAR(50),
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. Bảng mượn/trả sách
CREATE TABLE book_loans (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            book_id INT NOT NULL,
                            borrow_date DATE NOT NULL,
                            return_date DATE,
                            returned BOOLEAN DEFAULT FALSE,
                            CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users(id)
                                ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books(id)
                                ON DELETE CASCADE ON UPDATE CASCADE
);

-- 5. Trigger: giảm số lượng sách khi mượn
DELIMITER $$
CREATE TRIGGER trg_after_borrow
    AFTER INSERT ON book_loans
    FOR EACH ROW
BEGIN
    UPDATE books
    SET available_quantity = available_quantity - 1
    WHERE id = NEW.book_id AND available_quantity > 0;
END;
$$
DELIMITER ;

-- 6. Trigger: tăng số lượng sách khi trả
DELIMITER $$
CREATE TRIGGER trg_after_return
    AFTER UPDATE ON book_loans
    FOR EACH ROW
BEGIN
    IF NEW.returned = TRUE AND OLD.returned = FALSE THEN
    UPDATE books
    SET available_quantity = available_quantity + 1
    WHERE id = NEW.book_id;
END IF;
END;
$$
DELIMITER ;
