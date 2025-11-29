-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT 
  NULLIF(1, 1) AS FK.PKTABLE_CAT,
  FK.PKTABLE_SCHEM,
  FK.PKTABLE_NAME,
  FK.PKCOLUMN_NAME,
  NULLIF(1, 1) AS FK.FKTABLE_CAT,
  FK.FKTABLE_SCHEM,
  FK.FKTABLE_NAME,
  FK.FKCOLUMN_NAME,
  FK.KEY_SEQ,
  FK.UPDATE_RULE,
  FK.DELETE_RULE,
  FK.FK_NAME,
  FK.PK_NAME,
  FK.DEFERRABILITY,
  FK.UNIQUE_OR_PRIMARY
FROM
  SYSIBM.SQLFOREIGNKEYS FK
  INNER JOIN SYSCAT.TABLES T
    ON T.TABSCHEMA = FK.FKTABLE_SCHEM AND T.TABNAME = FK.FKTABLE_NAME
WHERE
  T.TYPE != 'A' -- Do not include imported foreign keys for synonyms   
WITH UR
