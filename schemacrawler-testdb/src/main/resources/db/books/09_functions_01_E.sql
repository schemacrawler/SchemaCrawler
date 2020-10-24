-- Functions
-- SAP HANA syntax
CREATE FUNCTION CustomAdd(One INTEGER, Two INTEGER)
  RETURNS CustomAdd INTEGER
AS
BEGIN
  CustomAdd = One + Two;
END
