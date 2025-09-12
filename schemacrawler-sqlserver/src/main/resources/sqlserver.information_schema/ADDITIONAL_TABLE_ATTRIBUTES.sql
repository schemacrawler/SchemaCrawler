-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    DB_NAME() AS TABLE_CATALOG,
    SCHEMA_NAME(O.SCHEMA_ID) AS TABLE_SCHEMA,
    O.NAME AS TABLE_NAME,
    EP.VALUE AS REMARKS
FROM
    SYS.ALL_OBJECTS O
    INNER JOIN SYS.EXTENDED_PROPERTIES EP
        ON O.OBJECT_ID = EP.MAJOR_ID AND EP.MINOR_ID = 0
WHERE
    O.IS_MS_SHIPPED != 1
    AND O.TYPE = 'U'
    AND EP.CLASS = 1
    AND EP.NAME = 'MS_Description'
WHERE
   SCHEMA_NAME(O.SCHEMA_ID) = '${schema-name}'
