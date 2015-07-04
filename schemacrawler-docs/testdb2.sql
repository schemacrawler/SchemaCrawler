CREATE TABLE Authors
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
  CONSTRAINT PK_Authors PRIMARY KEY (Id)
)
;

CREATE TABLE Books
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  Description VARCHAR(512),
  PublisherId INTEGER NOT NULL,
  PublicationDate DATE,
  Price FLOAT,
  PreviousEditionId INTEGER,  
  CONSTRAINT PK_Books PRIMARY KEY (Id),
  CONSTRAINT FK_PreviousEdition FOREIGN KEY (PreviousEditionId) REFERENCES Books (Id)
)
;

CREATE TABLE BookAuthors
(
  BookId INTEGER NOT NULL,
  AuthorId INTEGER NOT NULL,
  CONSTRAINT FK_Y_Book FOREIGN KEY (BookId) REFERENCES Books (Id),
  CONSTRAINT FK_Z_Author FOREIGN KEY (AuthorId) REFERENCES Authors (Id)
)
;

CREATE TABLE Extra
(
  ExtraColumn INTEGER
);
