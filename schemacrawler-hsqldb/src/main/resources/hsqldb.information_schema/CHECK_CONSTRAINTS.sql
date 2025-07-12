-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  *
FROM
  INFORMATION_SCHEMA.CHECK_CONSTRAINTS
ORDER BY
  CONSTRAINT_CATALOG,
  CONSTRAINT_SCHEMA,
  CONSTRAINT_NAME
