-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures
-- Microsoft SQL Server syntax
CREATE PROCEDURE GetBooksCount
    @BooksCount INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
	  @BooksCount = COUNT(*)
    FROM 
	  BOOKS.dbo.Books
END
@

EXEC sp_recompile 'GetBooksCount'
@
