-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with default for a column, and a check constraint
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
  CONSTRAINT PK_Authors PRIMARY KEY (Id)
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
  CONSTRAINT PK_Books PRIMARY KEY (Id),
  CONSTRAINT FK_PreviousEdition FOREIGN KEY (PreviousEditionId) REFERENCES Books (Id),
  CONSTRAINT U_PreviousEdition UNIQUE (PreviousEditionId)
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
  CONSTRAINT Z_FK_Author FOREIGN KEY (AuthorId) REFERENCES Authors (Id)
)
;
