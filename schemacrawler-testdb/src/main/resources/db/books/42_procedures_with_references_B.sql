-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures referencing tables
-- Oracle syntax
CREATE OR REPLACE PROCEDURE GetBooksCount(BooksCount OUT NUMBER) 
IS
BEGIN
    SELECT COUNT(*) INTO BooksCount
    FROM BOOKS.BOOKS;
END;
@

ALTER PROCEDURE GetBooksCount COMPILE
@
