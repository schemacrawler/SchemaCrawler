-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with auto-incremented column
-- with an unnamed primary key
CREATE TABLE Publishers
(
  Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  Publisher VARCHAR(255)
)
;
