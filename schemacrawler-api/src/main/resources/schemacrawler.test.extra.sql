-- Triggers
CREATE TRIGGER TRG_Authors BEFORE DELETE ON Authors FOR EACH ROW CALL "org.hsqldb.sample.TriggerSample";

-- Synonyms?
CREATE ALIAS ABS FOR "java.lang.Math.abs"

-- Create the next schema
CREATE SCHEMA SALES AUTHORIZATION DBA;
SET SCHEMA SALES;
