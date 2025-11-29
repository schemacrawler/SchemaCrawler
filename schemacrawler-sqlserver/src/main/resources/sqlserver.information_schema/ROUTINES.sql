-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    R.ROUTINE_CATALOG,
    R.ROUTINE_SCHEMA,
    R.ROUTINE_NAME,
    NULL AS SPECIFIC_NAME,
    R.ROUTINE_BODY,
    OBJECT_DEFINITION(OBJECT_ID(R.ROUTINE_SCHEMA + '.' + R.ROUTINE_NAME)) 
      AS ROUTINE_DEFINITION
FROM
    INFORMATION_SCHEMA.ROUTINES R
WHERE
   R.ROUTINE_SCHEMA = '${schema-name}'
