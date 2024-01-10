-- Triggers
-- HyperSQL, MySQL and DB2 syntax
CREATE TRIGGER TRG_Authors 
  AFTER DELETE ON Authors 
  FOR EACH ROW 
    UPDATE Publishers 
      SET Publisher = 'Jacob' 
      WHERE Publisher = 'John'
