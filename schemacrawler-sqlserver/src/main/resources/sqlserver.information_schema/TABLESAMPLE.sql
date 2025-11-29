-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0


-- TABLESAMPLE quickly selects ~300 rows, then NEWID() randomizes and selects 10
WITH SampledRows AS (
  SELECT 
    *
  FROM 
    ${table}
  TABLESAMPLE (300 ROWS)
)
SELECT TOP 10
  ${basiccolumns}
FROM 
  SampledRows
ORDER BY 
  NEWID()
