-- Stored procedures
-- Informix syntax
CREATE PROCEDURE New_Publisher(Publisher VARCHAR(50))
SPECIFIC New_Publisher_Force_Value;
  UPDATE Publisher
    SET Publisher = 'New Publisher';
END PROCEDURE
@

CREATE PROCEDURE New_Publisher(Publisher VARCHAR(50), New_Publisher VARCHAR(50))
  UPDATE Publisher
    SET Publisher = 'New Publisher';
END PROCEDURE
@
