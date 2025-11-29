-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Triggers
-- Informix syntax

CREATE TRIGGER TRG_Authors 
DELETE ON Authors
  FOR EACH ROW
  (
    UPDATE Publishers
      SET Publisher = 'Jacob'
      WHERE Publisher = 'John'
  )
;
