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
    ('To Kill a Mockingbird', 'Harper Lee', 8.99, 'A novel about racial injustice in the Deep South.', 'mockingbird.jpg'),
    ('1984', 'George Orwell', 15.99, 'A dystopian novel set in a totalitarian regime.', '1984.jpg'),
    ('The Great Gatsby', 'F. Scott Fitzgerald', 10.99, 'A classic novel of the Jazz Age.', 'great-gatsby.jpg'),
    ('Moby Dick', 'Herman Melville', 12.50, 'A narrative of a sailor’s adventures in pursuit of a giant whale.', 'moby-dick.jpg'),
    ('Pride and Prejudice', 'Jane Austen', 9.99, 'A romantic novel that critiques the British landed gentry.', 'pride-prejudice.jpg'),
    ('Harry Potter and the Sorcerer\'s Stone', 'J.K. Rowling', 20.99, 'The first book in the magical Harry Potter series.', 'harry-potter.jpg'),
    ('The Catcher in the Rye', 'J.D. Salinger', 13.50, 'A novel about teenage angst and alienation.', 'catcher-rye.jpg'),
    ('The Hobbit', 'J.R.R. Tolkien', 14.99, 'A fantasy novel about the adventures of Bilbo Baggins.', 'hobbit.jpg'),
    ('Fahrenheit 451', 'Ray Bradbury', 11.99, 'A dystopian novel about a future where books are banned.', 'fahrenheit-451.jpg'),
    ('Jane Eyre', 'Charlotte Brontë', 10.50, 'A novel about a young orphan’s growth and independence.', 'jane-eyre.jpg'),
    ('The Lord of the Rings', 'J.R.R. Tolkien', 25.99, 'An epic fantasy trilogy about the struggle to destroy a powerful ring.', 'lord-of-rings.jpg'),
    ('Wuthering Heights', 'Emily Brontë', 12.00, 'A tale of passion and revenge on the Yorkshire moors.', 'wuthering-heights.jpg'),
    ('Brave New World', 'Aldous Huxley', 13.99, 'A dystopian novel exploring a technologically advanced society.', 'brave-new-world.jpg'),
    ('The Chronicles of Narnia', 'C.S. Lewis', 16.50, 'A fantasy series about the magical land of Narnia.', 'chronicles-narnia.jpg'),
    ('Little Women', 'Louisa May Alcott', 11.00, 'A novel about the lives of four sisters growing up during the Civil War.', 'little-women.jpg'),
    ('Gone with the Wind', 'Margaret Mitchell', 18.99, 'A historical novel set during the American Civil War and Reconstruction.', 'gone-with-wind.jpg'),
    ('The Da Vinci Code', 'Dan Brown', 19.50, 'A thriller involving secret societies and historical mysteries.', 'da-vinci-code.jpg'),
    ('The Alchemist', 'Paulo Coelho', 14.00, 'A philosophical novel about a young shepherd’s quest for his personal legend.', 'alchemist.jpg'),
    ('The Shining', 'Stephen King', 15.00, 'A horror novel about a family staying in an isolated hotel with a dark history.', 'shining.jpg'),
    ('The Road', 'Cormac McCarthy', 17.50, 'A post-apocalyptic novel about a father and son’s journey through a desolate landscape.', 'road.jpg');
