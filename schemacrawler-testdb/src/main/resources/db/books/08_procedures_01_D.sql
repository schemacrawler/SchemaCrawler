-- Stored procedures 
-- Oracle syntax
CREATE OR REPLACE PROCEDURE New_Publisher(NewPublisher IN VARCHAR2, Publisher OUT VARCHAR2)
IS
BEGIN
  Publisher := NewPublisher;
END;
