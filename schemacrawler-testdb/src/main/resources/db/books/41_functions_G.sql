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
