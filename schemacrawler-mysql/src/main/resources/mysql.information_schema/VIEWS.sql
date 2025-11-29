-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  TABLE_SCHEMA AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  TABLE_NAME,
  VIEW_DEFINITION
FROM
  INFORMATION_SCHEMA.VIEWS
ORDER BY
  TABLE_CATALOG,
  TABLE_SCHEMA,
  TABLE_NAME
