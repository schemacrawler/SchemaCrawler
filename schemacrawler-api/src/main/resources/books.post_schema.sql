-- Stored procedures
CREATE PROCEDURE New_Publisher(OUT Publisher VARCHAR(50))
  SET Publisher = 'New Publisher'
;
  
-- Triggers
CREATE TRIGGER TRG_Authors AFTER DELETE ON Authors FOR EACH ROW UPDATE Publishers SET Publisher = 'Jacob' WHERE Publisher = 'John'
;
  
