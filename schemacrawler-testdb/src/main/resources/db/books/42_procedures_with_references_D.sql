-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures referencing tables
-- HyperSQL syntax
CREATE PROCEDURE GetBooksCount(OUT BooksCount INT)
READS SQL DATA
BEGIN ATOMIC
  SELECT COUNT(*) INTO BooksCount
  FROM PUBLIC.BOOKS.BOOKS;
END
