-- Triggers
CREATE TRIGGER TRG_Authors BEFORE DELETE ON Authors FOR EACH ROW CALL "org.hsqldb.sample.TriggerSample";
