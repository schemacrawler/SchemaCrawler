-- Stored procedures 
CREATE PROCEDURE BOOKS.New_Publisher(IN NewPublisher VARCHAR(50), OUT Publisher VARCHAR(50))
  SET Publisher = NewPublisher
;

-- Functions
CREATE FUNCTION BOOKS.CustomAdd(One INT)
  RETURNS INT
  RETURN CustomAdd(One, 1)
;
