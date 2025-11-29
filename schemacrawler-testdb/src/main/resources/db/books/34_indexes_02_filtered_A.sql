-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Index with filter condition
-- PostgreSQL, SQLite syntax
CREATE INDEX IDX_USA_Authors
  ON Authors (Country)
  WHERE 
    Country = 'USA'
;
