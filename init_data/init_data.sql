INSERT INTO authors (name)
VALUES ('J.K. Rowling'),
       ('George R.R. Martin'),
       ('Haruki Murakami'),
       ('Nguyễn Nhật Ánh');

INSERT INTO categories (name)
VALUES ('Fantasy'),
       ('Science Fiction'),
       ('Literature'),
       ('Children');

INSERT INTO books (title, author_id, category_id, publisher, year_published, total_quantity, available_quantity)
VALUES ('Harry Potter and the Sorcerer''s Stone', 1, 1, 'Bloomsbury', 1997, 5, 5),
       ('A Game of Thrones', 2, 1, 'Bantam Books', 1996, 3, 3),
       ('Kafka on the Shore', 3, 3, 'Shinchosha', 2002, 4, 4),
       ('Mắt Biếc', 4, 4, 'NXB Trẻ', 1990, 6, 6);

INSERT INTO users (name, email, phone)
VALUES ('Alice Nguyen', 'alice@example.com', '0901234567'),
       ('Bob Tran', 'bob@example.com', '0912345678'),
       ('Charlie Le', 'charlie@example.com', '0987654321');

-- Alice mượn Harry Potter và chưa trả
INSERT INTO book_loans (user_id, book_id, borrow_date, returned)
VALUES (1, 1, '2025-07-01', 0);

-- Bob mượn Kafka và đã trả
INSERT INTO book_loans (user_id, book_id, borrow_date, return_date, returned)
VALUES (2, 3, '2025-06-15', '2025-07-10', 1);
