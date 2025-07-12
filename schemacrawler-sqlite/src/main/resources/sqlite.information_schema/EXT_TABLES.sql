-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  NULL AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  name AS TABLE_NAME,
  sql AS TABLE_DEFINITION
FROM
  sqlite_master
WHERE
  type = 'table'
ORDER BY
  name
