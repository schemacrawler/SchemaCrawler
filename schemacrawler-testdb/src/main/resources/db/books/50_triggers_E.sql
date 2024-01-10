-- Triggers
-- SQLite syntax
CREATE TRIGGER TRG_Authors 
AFTER DELETE 
ON Authors 
FOR EACH ROW 
BEGIN
  UPDATE Publishers 
    SET Publisher = 'Jacob' 
    WHERE Publisher = 'John';
END;
