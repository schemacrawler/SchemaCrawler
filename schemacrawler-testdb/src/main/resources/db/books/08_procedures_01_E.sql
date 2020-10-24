-- Stored procedures
-- SAP HANA syntax
CREATE OR REPLACE PROCEDURE New_Publisher(NewPublisher VARCHAR2, Publisher VARCHAR2)
AS
BEGIN
  Publisher = NewPublisher;
END
