-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  CURRENT_DATABASE()::INFORMATION_SCHEMA.SQL_IDENTIFIER AS VIEW_CATALOG,
  VIEW_SCHEMA,
  VIEW_NAME,
  NULL AS TABLE_CATALOG,
  TABLE_SCHEMA,
  TABLE_NAME
FROM
  INFORMATION_SCHEMA.VIEW_TABLE_USAGE
