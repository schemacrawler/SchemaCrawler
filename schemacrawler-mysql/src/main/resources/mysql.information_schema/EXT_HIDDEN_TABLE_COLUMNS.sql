-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  TABLE_SCHEMA AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  TABLE_NAME,
  COLUMN_NAME,
  COLUMN_COMMENT,
  EXTRA
FROM
  INFORMATION_SCHEMA.COLUMNS
WHERE
  UPPER(EXTRA) = 'INVISIBLE'
