-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures
-- Microsoft SQL Server syntax
CREATE FUNCTION CustomAdd(@One INT, @Two INT)
RETURNS INT
AS
BEGIN
  DECLARE @ReturnValue INT;
  SELECT @ReturnValue = @One + @Two;
  RETURN @ReturnValue;
END
@
