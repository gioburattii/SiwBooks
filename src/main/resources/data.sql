-- Script di inizializzazione per PostgreSQL
-- Questo file viene eseguito automaticamente da Spring Boot

-- Inserimento di autori di esempio (solo se non esistono già)
INSERT INTO authors (first_name, last_name, birth_date, nationality, photo_path) 
SELECT 'Luigi', 'Pirandello', '1867-06-28', 'Italiana', 'authors/757d8a0a-6586-44ef-9941-f596a8718fcf.jpeg'
WHERE NOT EXISTS (SELECT 1 FROM authors WHERE first_name = 'Luigi' AND last_name = 'Pirandello');

INSERT INTO authors (first_name, last_name, birth_date, nationality) 
SELECT 'Italo', 'Calvino', '1923-10-15', 'Italiana'
WHERE NOT EXISTS (SELECT 1 FROM authors WHERE first_name = 'Italo' AND last_name = 'Calvino');

INSERT INTO authors (first_name, last_name, birth_date, nationality) 
SELECT 'Umberto', 'Eco', '1932-01-05', 'Italiana'
WHERE NOT EXISTS (SELECT 1 FROM authors WHERE first_name = 'Umberto' AND last_name = 'Eco');

-- Inserimento di libri di esempio (solo se non esistono già)
INSERT INTO books (title, publication_year) 
SELECT 'Il fu Mattia Pascal', 1904
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'Il fu Mattia Pascal');

INSERT INTO books (title, publication_year) 
SELECT 'Le città invisibili', 1972
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'Le città invisibili');

INSERT INTO books (title, publication_year) 
SELECT 'Il nome della rosa', 1980
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'Il nome della rosa'); 