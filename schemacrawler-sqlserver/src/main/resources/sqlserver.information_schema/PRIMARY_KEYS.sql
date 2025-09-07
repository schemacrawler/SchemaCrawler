-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    DB_NAME() AS TABLE_CAT,
    CAST(s.name AS SYSNAME) AS TABLE_SCHEM,
    CAST(t.name AS SYSNAME) AS TABLE_NAME,
    CAST(kc.name AS SYSNAME) AS PK_NAME,
    CAST(c.name AS SYSNAME) AS COLUMN_NAME,
    CAST(ic.key_ordinal AS SMALLINT) AS KEY_SEQ
FROM
    sys.key_constraints kc
    INNER JOIN sys.tables t
        ON kc.parent_object_id = t.object_id
    INNER JOIN sys.schemas s
        ON t.schema_id = s.schema_id
    INNER JOIN sys.indexes i
        ON i.object_id = t.object_id
            AND i.index_id  = kc.unique_index_id
    INNER JOIN sys.index_columns ic
        ON ic.object_id = i.object_id
            AND ic.index_id = i.index_id
    INNER JOIN sys.columns c
        ON c.object_id = ic.object_id
            AND c.column_id = ic.column_id
WHERE
    kc.type = 'PK'
    AND i.name IS NOT NULL
    AND i.is_hypothetical = 0
