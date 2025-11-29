-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with auto-incremented column
-- Informix syntax
CREATE TABLE Publishers
(
  ID SERIAL PRIMARY KEY,
  Publisher VARCHAR(255)
)
;
