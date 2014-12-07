CREATE TABLE BOOKS.Authors
(
  Id INTEGER NOT NULL,
  FirstName VARCHAR(20) NOT NULL,
  LastName VARCHAR(20) NOT NULL,
  Address1 VARCHAR(255),
  Address2 VARCHAR(255),
  City VARCHAR(50),
  State VARCHAR(2),
  PostalCode VARCHAR(10),
  Country VARCHAR(50),
  CONSTRAINT PK_Authors PRIMARY KEY (Id),
  CONSTRAINT CHECK_UPPERCASE_State CHECK (State=UPPER(State))
)
;

CREATE TABLE BOOKS.Books
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  Description VARCHAR(255),
  PublisherId INTEGER NOT NULL,
  PublicationDate DATE,
  Price FLOAT,
  PreviousEditionId INTEGER,  
  CONSTRAINT PK_Books PRIMARY KEY (Id),
  CONSTRAINT FK_PreviousEdition FOREIGN KEY (PreviousEditionId) REFERENCES Books (Id)
)
;

CREATE TABLE BOOKS.BookAuthors
(
  BookId INTEGER NOT NULL,
  AuthorId INTEGER NOT NULL,
  "UPDATE" CLOB,
  CONSTRAINT FK_Y_Book FOREIGN KEY (BookId) REFERENCES Books (Id),
  CONSTRAINT FK_Z_Author FOREIGN KEY (AuthorId) REFERENCES Authors (Id)
)
;

CREATE TABLE BOOKS."Global Counts"
(
  "Global Count" INTEGER
)
;

-- Views
CREATE VIEW BOOKS.AuthorsList AS SELECT Id, FirstName, LastName FROM Authors
;

-- Indices
CREATE UNIQUE INDEX UIDX_PreviousEdition ON BOOKS.Books(PreviousEditionId)
;
CREATE UNIQUE INDEX UIDX_BookAuthors ON BOOKS.BookAuthors(BookId, AuthorId)
;
CREATE INDEX IDX_B_Authors ON BOOKS.Authors(LastName, FirstName)
;
CREATE INDEX IDX_A_Authors ON BOOKS.Authors(City, State, PostalCode, Country)
;
