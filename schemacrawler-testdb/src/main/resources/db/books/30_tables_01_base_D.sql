-- Table with default for a column, and a check constraint
-- Informix syntax
CREATE TABLE Authors
(
  Id INTEGER NOT NULL,
  FirstName VARCHAR(20) NOT NULL,
  LastName VARCHAR(20) NOT NULL,
  Address1 VARCHAR(255),
  Address2 VARCHAR(255),
  City VARCHAR(50),
  State CHAR(2),
  PostalCode VARCHAR(10),
  Country VARCHAR(50) DEFAULT 'USA',
  PRIMARY KEY (Id) CONSTRAINT PK_Authors,
  CHECK (State=UPPER(State)) CONSTRAINT CHECK_UPPERCASE_State
)
;

-- Table with unique constraint, and self-referencing foreign key
CREATE TABLE Books
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  Description VARCHAR(255),
  PublisherId INTEGER NOT NULL,
  PublicationDate DATE,
  Price FLOAT,
  PreviousEditionId INTEGER,
  PRIMARY KEY (Id) CONSTRAINT PK_Books,
  FOREIGN KEY (PreviousEditionId) REFERENCES Books (Id) CONSTRAINT FK_PreviousEdition,
  UNIQUE (PreviousEditionId) CONSTRAINT U_PreviousEdition 
)
;

-- Contains unnamed foreign key
-- Foreign keys have a different natural and alphabetical sort order
CREATE TABLE BookAuthors
(
  BookId INTEGER NOT NULL,
  AuthorId INTEGER NOT NULL,
  SomeData VARCHAR(30),
  FOREIGN KEY (BookId) REFERENCES Books (Id),
  FOREIGN KEY (AuthorId) REFERENCES Authors (Id) CONSTRAINT Z_FK_Author
)
;
