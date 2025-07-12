-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  TABLE_CATALOG,
  TABLE_SCHEMA,
  TABLE_NAME,
  COLUMN_NAME,
  IS_GENERATED,
  GENERATION_EXPRESSION
FROM 
  INFORMATION_SCHEMA.COLUMNS
