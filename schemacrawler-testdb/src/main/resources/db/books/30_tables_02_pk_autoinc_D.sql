-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with identity column, generated
-- with an unnamed primary key
-- Oracle 12c syntax
CREATE TABLE Publishers
(
  Id INTEGER GENERATED ALWAYS AS IDENTITY,
  Publisher VARCHAR(255),
  PRIMARY KEY (Id)
)
;
