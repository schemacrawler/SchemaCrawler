-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Hive BOOKS database setup.
-- Use broadly supported Hive features while mirroring BOOKS structures.

DROP DATABASE IF EXISTS books CASCADE;
CREATE DATABASE books
COMMENT 'SchemaCrawler BOOKS test database for Hive';

USE books;

CREATE TABLE Publishers
(
  Id INT COMMENT 'Unique (internal) id for book publisher',
  Publisher STRING COMMENT 'Name of book publisher',
  CONSTRAINT PK_Publishers PRIMARY KEY (Id) DISABLE NOVALIDATE RELY
)
COMMENT 'List of book publishers'
STORED AS ORC;

CREATE TABLE Authors
(
  Id INT COMMENT 'Unique (internal) id for book author',
  FirstName STRING COMMENT 'Author first name',
  LastName STRING COMMENT 'Author last name',
  Address1 STRING,
  Address2 STRING,
  City STRING,
  State STRING,
  PostalCode STRING,
  Country STRING COMMENT 'Author country',
  CONSTRAINT PK_Authors PRIMARY KEY (Id) DISABLE NOVALIDATE RELY
)
COMMENT 'Contact details for book authors'
STORED AS ORC;

CREATE TABLE Books
(
  Id INT COMMENT 'Unique (internal) id for book',
  Title STRING COMMENT 'Book title',
  Description STRING COMMENT 'Book description',
  PublisherId INT COMMENT 'Foreign key to the book publisher',
  PublicationDate DATE COMMENT 'Book publication date',
  Price DECIMAL(10,2) COMMENT 'Current price for the book',
  PreviousEditionId INT COMMENT 'Foreign key to previous edition',
  CONSTRAINT PK_Books PRIMARY KEY (Id) DISABLE NOVALIDATE RELY,
  CONSTRAINT FK_Books_Publishers FOREIGN KEY (PublisherId)
    REFERENCES Publishers (Id) DISABLE NOVALIDATE RELY,
  CONSTRAINT FK_PreviousEdition FOREIGN KEY (PreviousEditionId)
    REFERENCES Books (Id) DISABLE NOVALIDATE RELY
)
COMMENT 'Details for published books'
STORED AS ORC;

CREATE TABLE BookAuthors
(
  BookId INT COMMENT 'Foreign key to the book',
  AuthorId INT COMMENT 'Foreign key to the author',
  SomeData STRING COMMENT 'Additional relationship details',
  CONSTRAINT PK_BookAuthors PRIMARY KEY (BookId, AuthorId)
    DISABLE NOVALIDATE RELY,
  CONSTRAINT FK_BookAuthors_Books FOREIGN KEY (BookId)
    REFERENCES Books (Id) DISABLE NOVALIDATE RELY,
  CONSTRAINT FK_BookAuthors_Authors FOREIGN KEY (AuthorId)
    REFERENCES Authors (Id) DISABLE NOVALIDATE RELY
)
COMMENT 'Relationship between books and their authors'
STORED AS ORC;

CREATE TABLE BookTags
(
  BookTagId INT,
  BookId INT,
  BookTag STRING
)
COMMENT 'Book tags'
PARTITIONED BY (TagGroup STRING)
STORED AS ORC;

CREATE VIEW AuthorsList AS
SELECT Id, FirstName, LastName
FROM Authors;
