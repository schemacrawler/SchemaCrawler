-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  VIEW_SCHEMA AS VIEW_CATALOG,
  NULL AS VIEW_SCHEMA,
  VIEW_NAME,
  TABLE_SCHEMA AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  TABLE_NAME
FROM
  INFORMATION_SCHEMA.VIEW_TABLE_USAGE
ORDER BY
  VIEW_SCHEMA,
  VIEW_NAME,
  TABLE_NAME
