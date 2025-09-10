-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    R.ROUTINE_CATALOG,
    R.ROUTINE_SCHEMA,
    R.ROUTINE_NAME,
    NULL AS SPECIFIC_NAME,
    DB_NAME() AS REFERENCED_OBJECT_CATALOG,
    OBJECT_SCHEMA_NAME(d.referenced_id, DB_ID(DB_NAME())) AS REFERENCED_OBJECT_SCHEMA,
    o.name AS REFERENCED_OBJECT_NAME,
    o.name AS REFERENCED_OBJECT_SPECIFIC_NAME,
    o.type_desc AS REFERENCED_OBJECT_TYPE
FROM
    INFORMATION_SCHEMA.ROUTINES R
    INNER JOIN sys.sql_expression_dependencies d
        ON OBJECT_ID(R.ROUTINE_SCHEMA + '.' + R.ROUTINE_NAME) = d.referencing_id
    INNER JOIN sys.objects o
        ON d.referenced_id = o.object_id
