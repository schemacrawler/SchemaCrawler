-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT 
  ${basiccolumns}
FROM 
  ${table} 
TABLESAMPLE BERNOULLI(70)
LIMIT 10
