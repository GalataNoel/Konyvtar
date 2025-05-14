-- Szerzők beszúrása
INSERT INTO author (id, name) VALUES
(1, 'J.K. Rowling'),
(2, 'George Orwell'),
(3, 'Agatha Christie');

-- Könyvek beszúrása (az author_id a szerzőkhez kapcsolódik)
INSERT INTO book (id, title, isbn, author_id) VALUES
(1, 'Harry Potter és a Bölcsek Köve', '963-8386-87-0', 1),
(2, '1984', '978-963-07-5382-5', 2),
(3, 'Tíz kicsi néger', '963-07-8093-7', 3),
(4, 'Harry Potter és a Titkok Kamrája', '963-8386-88-9', 1);

-- ID generátor resetelése (H2 specifikus)
ALTER SEQUENCE IF EXISTS author_seq RESTART WITH 4;
ALTER SEQUENCE IF EXISTS book_seq RESTART WITH 5;