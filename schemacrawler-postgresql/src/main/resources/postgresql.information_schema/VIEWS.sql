-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  CURRENT_DATABASE()::INFORMATION_SCHEMA.SQL_IDENTIFIER AS TABLE_CATALOG,
  TABLE_SCHEMA,
  TABLE_NAME,
  VIEW_DEFINITION,
  CHECK_OPTION,
  IS_UPDATABLE
FROM
  INFORMATION_SCHEMA.VIEWS
ORDER BY
  TABLE_CATALOG,
  TABLE_SCHEMA,
  TABLE_NAME
