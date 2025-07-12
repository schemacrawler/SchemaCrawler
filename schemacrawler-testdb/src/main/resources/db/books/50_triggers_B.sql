-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Triggers
-- Oracle syntax
CREATE TRIGGER TRG_Authors 
AFTER INSERT OR DELETE 
ON Authors 
FOR EACH ROW 
BEGIN
  UPDATE Publishers 
    SET Publisher = 'Jacob' 
    WHERE Publisher = 'John';
END;
