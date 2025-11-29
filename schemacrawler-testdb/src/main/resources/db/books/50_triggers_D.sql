-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Triggers
-- PostgreSQL syntax

CREATE FUNCTION trg_authors() RETURNS trigger AS $trg_authors$
  BEGIN
    RAISE NOTICE 'LOGGED FROM TRIGGER';
    RETURN NEW;
  END;
$trg_authors$ LANGUAGE plpgsql
@

CREATE TRIGGER TRG_Authors BEFORE INSERT OR DELETE ON Authors
  FOR EACH ROW EXECUTE PROCEDURE trg_authors();
@
