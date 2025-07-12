-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with identity column, generated
-- with an unnamed primary key
-- PostgreSQL and MySQL syntax
CREATE TABLE Publishers
(
  Id SERIAL,
  Publisher VARCHAR(255),
  PRIMARY KEY (Id)
)
;
