-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Microsoft SQL Server syntax

-- Stored procedures
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
