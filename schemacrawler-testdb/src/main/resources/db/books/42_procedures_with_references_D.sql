-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures referencing tables
<<<<<<< Updated upstream
-- HyperSQL syntax
CREATE PROCEDURE GetBooksCount(OUT BooksCount INT)
READS SQL DATA
BEGIN ATOMIC
  SELECT COUNT(*) INTO BooksCount
  FROM PUBLIC.BOOKS.BOOKS;
END
@
=======
-- IBM DB2 syntax
CREATE PROCEDURE GetBooksCount(OUT BooksCount INTEGER)
BEGIN ATOMIC
    SELECT COUNT(*) INTO BooksCount
    FROM BOOKS.BOOKS;
END;
>>>>>>> Stashed changes
