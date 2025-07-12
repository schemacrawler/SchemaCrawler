-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Functions
CREATE FUNCTION CustomAdd(One INT, Two INT)
  RETURNS INT
  RETURN One + Two
;

-- Functions
CREATE FUNCTION CustomAdd(One INT)
  RETURNS INT
  RETURN CustomAdd(One, 1)
;
