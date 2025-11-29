-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT DISTINCT
  INDEX_SCHEMA AS INDEX_CATALOG,
  NULL AS INDEX_SCHEMA,
  INDEX_NAME,
  TABLE_SCHEMA AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  TABLE_NAME,
  INDEX_COMMENT AS REMARKS
FROM
  INFORMATION_SCHEMA.STATISTICS
