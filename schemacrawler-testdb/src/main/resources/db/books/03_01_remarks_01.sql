-- Remarks
COMMENT ON TABLE BOOKS.Publishers IS 'List of book publishers'
;
COMMENT ON COLUMN BOOKS.Publishers.Id IS 'Unique (internal) id for book publisher'
;
COMMENT ON COLUMN BOOKS.Publishers.Publisher IS 'Name of book publisher'
;

COMMENT ON TABLE BOOKS.Authors IS 'Contact details for book authors'
;

COMMENT ON TABLE BOOKS.Books IS 'Details for published books'
;
COMMENT ON COLUMN BOOKS.Books.Id IS 'Unique (internal) id for book'
;
COMMENT ON COLUMN BOOKS.Books.Title IS 'Book title'
;
COMMENT ON COLUMN BOOKS.Books.Description IS 'Book description'
;
COMMENT ON COLUMN BOOKS.Books.PublisherId IS 'Foreign key to the book publisher'
;
COMMENT ON COLUMN BOOKS.Books.PublicationDate IS 'Book publication date'
;
COMMENT ON COLUMN BOOKS.Books.Price IS 'Current price for the book'
;

COMMENT ON TABLE BOOKS.BookAuthors IS 'Relationship between books and their authors, along with the latest updated information'
;