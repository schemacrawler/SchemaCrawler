-- Triggers
CREATE TRIGGER TRG_Authors BEFORE DELETE ON Authors FOR EACH ROW CALL "org.hsqldb.sample.TriggerSample";

-- Synonyms?
-- CREATE ALIAS ABS FOR "java.lang.Math.abs"
SET SCHEMA BOOKS;