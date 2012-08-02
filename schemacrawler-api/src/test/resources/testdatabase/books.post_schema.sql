-- Remarks
COMMENT ON TABLE  Publishers IS 'List of book publishers'
;
COMMENT ON COLUMN Publishers.Id IS 'Unique (internal) id for book publisher'
;
COMMENT ON COLUMN Publishers.Publisher IS 'Name of book publisher'
;

COMMENT ON TABLE  Authors IS 'Contact details for book authors'
;

COMMENT ON TABLE  Books IS 'Details for published books'
;
COMMENT ON COLUMN Books.Id IS 'Unique (internal) id for book'
;
COMMENT ON COLUMN Books.Title IS 'Book title'
;
COMMENT ON COLUMN Books.Description IS 'Book description'
;
COMMENT ON COLUMN Books.PublisherId IS 'Foreign key to the book publisher'
;
COMMENT ON COLUMN Books.PublicationDate IS 'Book publication date'
;
COMMENT ON COLUMN Books.Price IS 'Current price for the book'
;

COMMENT ON TABLE  BookAuthors IS 'Relationship between books and their authors, along with the latest updated information'
;

-- Stored procedures
CREATE PROCEDURE New_Publisher(OUT Publisher VARCHAR(50))
  SET Publisher = 'New Publisher'
;
  
CREATE PROCEDURE New_Publisher(IN NewPublisher VARCHAR(50), OUT Publisher VARCHAR(50))
  SET Publisher = NewPublisher
;

-- Triggers
CREATE TRIGGER TRG_Authors AFTER DELETE ON Authors FOR EACH ROW UPDATE Publishers SET Publisher = 'Jacob' WHERE Publisher = 'John'
;

-- Domains
CREATE DOMAIN VALID_STRING AS VARCHAR(20) DEFAULT 'NO VALUE' CHECK (VALUE IS NOT NULL AND CHARACTER_LENGTH(VALUE) > 2);
