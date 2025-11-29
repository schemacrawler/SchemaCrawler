-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with auto-incremented column
-- DuckDB syntax
CREATE TABLE Publishers
(
  Id INT PRIMARY KEY DEFAULT NEXTVAL('Publisher_Id_Seq'),
  Publisher VARCHAR(255)
)
;
