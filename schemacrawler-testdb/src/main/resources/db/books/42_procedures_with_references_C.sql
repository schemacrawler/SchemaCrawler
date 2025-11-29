-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- IBM DB2 syntax
CREATE PROCEDURE GetBooksCount(OUT BooksCount INTEGER)
BEGIN ATOMIC
    SELECT COUNT(*) INTO BooksCount
    FROM BOOKS.BOOKS;
END;
