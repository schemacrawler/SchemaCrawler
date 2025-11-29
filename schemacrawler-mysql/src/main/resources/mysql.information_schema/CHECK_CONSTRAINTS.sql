-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  CC.CONSTRAINT_SCHEMA AS CONSTRAINT_CATALOG,
  NULL AS CONSTRAINT_SCHEMA,
  CC.CONSTRAINT_NAME,
  CC.CHECK_CLAUSE
FROM
  INFORMATION_SCHEMA.CHECK_CONSTRAINTS CC
