-- Triggers
-- PostgreSQL syntax

CREATE FUNCTION trg_authors() RETURNS trigger AS $trg_authors$
    BEGIN
        RAISE NOTICE 'LOGGED FROM TRIGGER';
        RETURN NEW;
    END;
$trg_authors$ LANGUAGE plpgsql
@

CREATE TRIGGER TRG_Authors BEFORE UPDATE OR DELETE ON Authors
    FOR EACH ROW EXECUTE PROCEDURE trg_authors();
@
