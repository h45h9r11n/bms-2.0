CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(255)
    );

INSERT INTO comments (book_id, user_id, content) values (1, 1, 'This is a great book!'),
(1, 2, 'I love this book!'),
(2, 1, 'This is a great book!'),
(2, 2, 'I love this book!');