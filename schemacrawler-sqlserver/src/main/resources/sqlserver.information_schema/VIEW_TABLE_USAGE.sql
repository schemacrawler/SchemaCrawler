-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    DB_NAME()
        AS VIEW_CATALOG,
    vs.name
        AS VIEW_SCHEMA,
    v.name
        AS VIEW_NAME,
    COALESCE(sed.referenced_database_name, DB_NAME())
        AS TABLE_CATALOG,
    ISNULL(sed.referenced_schema_name, ts.name)
        AS TABLE_SCHEMA,
    ISNULL(sed.referenced_entity_name, t.name)
        AS TABLE_NAME
FROM
    sys.views v
    INNER JOIN sys.schemas vs
        ON v.schema_id = vs.schema_id
    INNER JOIN sys.sql_expression_dependencies sed
        ON sed.referencing_id = v.object_id
    INNER JOIN sys.objects t
        ON sed.referenced_id = t.object_id
    INNER JOIN sys.schemas ts
        ON t.schema_id = ts.schema_id
WHERE
   vs.name = '${schema-name}'
