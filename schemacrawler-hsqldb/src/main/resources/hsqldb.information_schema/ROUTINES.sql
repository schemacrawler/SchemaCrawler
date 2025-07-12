-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  *
FROM
  INFORMATION_SCHEMA.ROUTINES
ORDER BY
  ROUTINE_CATALOG,
  ROUTINE_SCHEMA,
  ROUTINE_NAME  
