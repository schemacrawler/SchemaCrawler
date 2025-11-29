-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  NULL AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  name AS TABLE_NAME,
  sql AS VIEW_DEFINITION,
  'UNKNOWN' AS CHECK_OPTION,
  'N' AS IS_UPDATABLE
FROM
  sqlite_master
WHERE
  type = 'view'
ORDER BY
  name
