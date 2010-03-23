-- Triggers
CREATE TRIGGER TRG_Authors BEFORE DELETE ON Authors FOR EACH ROW CALL "org.hsqldb.sample.TriggerSample";

-- Create the next schema
CREATE SCHEMA SALES AUTHORIZATION DBA;
SET SCHEMA SALES;
