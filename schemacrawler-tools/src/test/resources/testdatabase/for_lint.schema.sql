CREATE TABLE Publishers
(
  Id INTEGER NOT NULL,
  Publisher VARCHAR(255),
  CONSTRAINT PK_Publishers PRIMARY KEY (Id)
)
;

CREATE TABLE Authors
(
  Id INTEGER NOT NULL,
  FirstName VARCHAR(20) NOT NULL,
  LastName VARCHAR(20) NOT NULL,
  Address1 VARCHAR(255),
  Address2 VARCHAR(255) DEFAULT NULL NOT NULL,
  City VARCHAR(50),
  State VARCHAR(2),
  PostalCode VARCHAR(10),
  Country VARCHAR(50) DEFAULT NULL,
  Phone1 VARCHAR(10),
  Phone2 VARCHAR(15),
  Email1 VARCHAR(10),
  Email2 INT,
  Fax VARCHAR(10),
  Fax3 INT,
  HomeEmail11 VARCHAR(10),
  HomeEmail12 VARCHAR(10),
  CONSTRAINT PK_Authors PRIMARY KEY (Id),
  CONSTRAINT CHECK_UPPERCASE_State CHECK (State=UPPER(State))
)
;

CREATE TABLE Books
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  Description VARCHAR(255),
  PublisherId INTEGER NOT NULL,
  PublicationDate DATE,
  Price FLOAT,
  CONSTRAINT PK_Books PRIMARY KEY (Id)
)
;

CREATE TABLE BookAuthors
(
  BookId SMALLINT NOT NULL,
  AuthorId INTEGER NOT NULL,
  "UPDATE" CLOB,
  CONSTRAINT FK_Y_Book FOREIGN KEY (BookId) REFERENCES Books (Id),
  CONSTRAINT FK_Z_Author FOREIGN KEY (AuthorId) REFERENCES Authors (Id)
)
;

CREATE TABLE "Global Counts"
(
  "Global Count" INTEGER
)
;

CREATE TABLE NO_COLS
(
)
;

-- Indices
CREATE UNIQUE INDEX UIDX_BookAuthors ON BookAuthors(BookId, AuthorId)
;
CREATE INDEX IDX_B_Authors ON Authors(LastName, FirstName)
;
CREATE INDEX IDX_A_Authors ON Authors(City, State, PostalCode, Country)
;
CREATE UNIQUE INDEX IDX_U_Authors ON Authors(Email1, Country)
;
