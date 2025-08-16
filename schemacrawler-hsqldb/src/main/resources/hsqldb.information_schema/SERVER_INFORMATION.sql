-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  'CATALOG_NAME' AS NAME,
  CATALOG_NAME AS VALUE,
  '' AS DESCRIPTION
FROM
  INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME
