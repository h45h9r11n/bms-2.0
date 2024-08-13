CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    price DECIMAL(5, 2) NOT NULL,
    description TEXT,
    image VARCHAR(255)
);

INSERT INTO books (title, author, price, description, image)
VALUES
    ('The Great Gatsby', 'F. Scott Fitzgerald', 10.99, 'A classic novel of the Jazz Age.', 'gatsby.jpg'),
    ('To Kill a Mockingbird', 'Harper Lee', 8.99, 'A novel about racial injustice in the Deep South.', 'mockingbird.jpg');
