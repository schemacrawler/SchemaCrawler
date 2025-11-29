-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Triggers
-- Microsoft SQL Server syntax
CREATE TRIGGER TRG_Authors 
  ON Authors 
  AFTER INSERT, DELETE 
  AS 
    UPDATE Publishers 
      SET Publisher = 'Jacob' 
      WHERE Publisher = 'John'
;
