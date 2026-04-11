-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- ClickHouse Books database setup
-- ClickHouse uses databases (not schemas) and requires the MergeTree engine.
-- Foreign key constraints and sequences are not supported in ClickHouse.

DROP DATABASE IF EXISTS books;

CREATE DATABASE books;

CREATE TABLE books.Authors
(
  Id          Int32,
  FirstName   String,
  LastName    String,
  Address1    Nullable(String),
  Address2    Nullable(String),
  City        Nullable(String),
  State       Nullable(String),
  PostalCode  Nullable(String),
  Country     String DEFAULT 'USA'
)
ENGINE = MergeTree()
ORDER BY Id;

CREATE TABLE books.Publishers
(
  Id        Int32,
  Publisher Nullable(String)
)
ENGINE = MergeTree()
ORDER BY Id;

CREATE TABLE books.Books
(
  Id                Int32,
  Title             String,
  Description       Nullable(String),
  PublisherId       Int32,
  PublicationDate   Nullable(Date),
  Price             Nullable(Float64),
  PreviousEditionId Nullable(Int32)
)
ENGINE = MergeTree()
ORDER BY Id;

CREATE TABLE books.BookAuthors
(
  BookId   Int32,
  AuthorId Int32,
  SomeData Nullable(String)
)
ENGINE = MergeTree()
ORDER BY (BookId, AuthorId);

CREATE TABLE books.BookTags
(
  BookTagId Int32,
  BookId    Int32,
  BookTag   String
)
ENGINE = MergeTree()
ORDER BY BookTagId
