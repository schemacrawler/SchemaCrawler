-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  TABLE_SCHEMA AS TABLE_CATALOG,
  NULL AS TABLE_SCHEMA,
  TABLE_NAME,
  ENGINE,
  ROW_FORMAT,
  TABLE_ROWS,
  AVG_ROW_LENGTH,
  DATA_LENGTH,
  MAX_DATA_LENGTH,
  INDEX_LENGTH,
  DATA_FREE,
  AUTO_INCREMENT,
  TABLE_COLLATION,
  TABLE_COMMENT,
  CREATE_TIME
FROM
  INFORMATION_SCHEMA.TABLES
