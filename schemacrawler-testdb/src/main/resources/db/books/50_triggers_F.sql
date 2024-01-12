-- Triggers
-- Microsoft SQL Server syntax
CREATE TRIGGER TRG_Authors 
  ON Authors 
  AFTER INSERT, DELETE 
  AS 
    UPDATE Publishers 
      SET Publisher = 'Jacob' 
      WHERE Publisher = 'John'
;
