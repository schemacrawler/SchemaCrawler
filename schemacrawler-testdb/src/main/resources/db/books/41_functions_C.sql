-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Functions
-- Oracle syntax
CREATE OR REPLACE FUNCTION CustomAdd(One IN INTEGER) 
RETURN INTEGER
AS 
BEGIN
  RETURN One + 1;
END;
@

CREATE OR REPLACE FUNCTION CustomAdd(One IN INTEGER, Two IN INTEGER) 
RETURN INTEGER
AS 
BEGIN
  RETURN One + Two;
END;
@
