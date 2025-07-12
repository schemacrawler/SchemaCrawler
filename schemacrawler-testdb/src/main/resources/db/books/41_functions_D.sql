-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Functions
-- PostgreSQL syntax
CREATE FUNCTION CustomAdd(One INTEGER) RETURNS INTEGER
  AS 'SELECT One + 1;'
  LANGUAGE SQL
  IMMUTABLE
  RETURNS NULL ON NULL INPUT
@

CREATE FUNCTION CustomAdd(One INTEGER, Two INTEGER) RETURNS INTEGER
  AS 'SELECT One + Two;'
  LANGUAGE SQL
  IMMUTABLE
  RETURNS NULL ON NULL INPUT
@
