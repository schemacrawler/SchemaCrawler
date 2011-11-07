
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
  BookId BIGINT NOT NULL,
  CONSTRAINT PK_Authors PRIMARY KEY (Id),
  CONSTRAINT CHECK_UPPERCASE_State CHECK (State=UPPER(State))
)
;

CREATE TABLE Books
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  Description VARCHAR(255),
  AuthorId INTEGER NOT NULL,
  PublicationDate DATE,
  Price FLOAT,
  "UPDATE" CLOB,
  CONSTRAINT PK_Books PRIMARY KEY (Id),
  CONSTRAINT FK_Books_Author FOREIGN KEY (AuthorId) REFERENCES Authors (Id)
)
;

CREATE TABLE "Global Counts"
(
  "Global Count" INTEGER
)
;

CREATE TABLE "No_Columns"
(
)
;

ALTER TABLE Authors ADD CONSTRAINT FK_Authors_Book FOREIGN KEY (BookId) REFERENCES Books (Id);

-- Indices
CREATE INDEX IDX_B_Authors ON Authors(LastName, FirstName)
;
CREATE INDEX IDX_A_Authors ON Authors(City, State, PostalCode, Country)
;
CREATE INDEX IDX_A1_Authors ON Authors(City, State)
;
CREATE UNIQUE INDEX IDX_U_Authors ON Authors(Email1, Country)
;
