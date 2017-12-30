-- Stored procedures 
CREATE PROCEDURE New_Publisher(IN NewPublisher VARCHAR(50), OUT Publisher VARCHAR(50))
  SET Publisher = NewPublisher
;
