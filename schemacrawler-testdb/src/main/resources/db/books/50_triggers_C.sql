-- Triggers
-- Informix syntax

CREATE TRIGGER TRG_Authors 
DELETE ON Authors
  FOR EACH ROW
  (
    UPDATE Publishers
      SET Publisher = 'Jacob'
      WHERE Publisher = 'John'
  )
;
