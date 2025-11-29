-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with auto-incremented column, generated
-- with an unnamed primary key
-- IBM DB2, Apache Derby and H2 syntax
CREATE TABLE Publishers
(
  Id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  Publisher VARCHAR(255)
)
;
