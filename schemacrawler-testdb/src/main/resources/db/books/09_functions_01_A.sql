-- Functions
CREATE FUNCTION CustomAdd(One INT, Two INT)
  RETURNS INT
  RETURN One + Two
;

-- Functions
CREATE FUNCTION CustomAdd(One INT)
  RETURNS INT
  RETURN CustomAdd(One, 1)
;
