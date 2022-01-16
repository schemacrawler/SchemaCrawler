-- Functions
-- Informix syntax
CREATE FUNCTION CustomAdd(One INT, Two INT)
  RETURNING INT;
  RETURN One + Two;
END FUNCTION  
@

CREATE FUNCTION CustomAdd(One INT)
  RETURNING INT;
  RETURN CustomAdd(One, 1);
END FUNCTION  
@
