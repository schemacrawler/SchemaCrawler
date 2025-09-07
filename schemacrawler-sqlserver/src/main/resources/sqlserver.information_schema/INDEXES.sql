-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    DB_NAME() AS TABLE_CAT,
    CAST(s.name AS SYSNAME) AS TABLE_SCHEM,
    CAST(t.name AS SYSNAME) AS TABLE_NAME,
    CAST(CASE WHEN i.is_unique = 1 THEN 0 ELSE 1 END AS SMALLINT) AS NON_UNIQUE,
    CAST(NULL AS SYSNAME) AS INDEX_QUALIFIER,
    CAST(i.name AS SYSNAME) AS INDEX_NAME,
    CAST(CASE i.type
        WHEN 1 THEN 1  -- tableIndexClustered
        WHEN 7 THEN 2  -- tableIndexHashed
        ELSE 3         -- Default to tableIndexOther
    END AS SMALLINT) AS TYPE,
    CAST(ic.key_ordinal AS SMALLINT) AS ORDINAL_POSITION,
    CAST(c.name AS SYSNAME) AS COLUMN_NAME,
    CAST(CASE WHEN ic.is_descending_key = 1 THEN 'D' ELSE 'A' END AS VARCHAR(1)) AS ASC_OR_DESC,
    CAST(NULL AS INT) AS CARDINALITY,
    CAST(NULL AS INT) AS PAGES,
    CAST(i.filter_definition AS SYSNAME) AS FILTER_CONDITION,
    i.is_primary_key,
    i.is_unique_constraint,
    i.is_hypothetical,
    i.auto_created,
    i.type_desc
FROM
    sys.tables t
    INNER JOIN sys.schemas s
        ON t.schema_id = s.schema_id
    INNER JOIN sys.indexes i
        ON i.object_id = t.object_id
    INNER JOIN sys.index_columns ic
        ON ic.object_id = i.object_id
            AND ic.index_id = i.index_id
    INNER JOIN sys.columns c
        ON c.object_id = ic.object_id
            AND c.column_id = ic.column_id
WHERE
    i.name IS NOT NULL
    AND i.is_hypothetical = 0

UNION ALL

SELECT
    DB_NAME() AS TABLE_CAT,
    CAST(vs.name AS SYSNAME) AS TABLE_SCHEM,
    CAST(v.name AS SYSNAME) AS TABLE_NAME,
    CAST(CASE WHEN i.is_unique = 1 THEN 0 ELSE 1 END AS SMALLINT) AS NON_UNIQUE,
    CAST(NULL AS SYSNAME) AS INDEX_QUALIFIER,
    CAST(i.name AS SYSNAME) AS INDEX_NAME,
    CAST(CASE i.type
        WHEN 1 THEN 1  -- tableIndexClustered
        WHEN 7 THEN 2  -- tableIndexHashed
        ELSE 3         -- Default to tableIndexOther
    END AS SMALLINT) AS TYPE,
    CAST(ic.key_ordinal AS SMALLINT) AS ORDINAL_POSITION,
    CAST(c.name AS SYSNAME) AS COLUMN_NAME,
    CAST(CASE WHEN ic.is_descending_key = 1 THEN 'D' ELSE 'A' END AS VARCHAR(1)) AS ASC_OR_DESC,
    CAST(NULL AS INT) AS CARDINALITY,
    CAST(NULL AS INT) AS PAGES,
    CAST(i.filter_definition AS SYSNAME) AS FILTER_CONDITION,
    i.is_primary_key,
    i.is_unique_constraint,
    i.is_hypothetical,
    i.auto_created,
    i.type_desc
FROM
    sys.views v
    INNER JOIN sys.schemas vs
        ON v.schema_id = vs.schema_id
    INNER JOIN sys.indexes i
        ON i.object_id = v.object_id
    INNER JOIN sys.index_columns ic
        ON ic.object_id = i.object_id
            AND ic.index_id = i.index_id
    INNER JOIN sys.columns c
        ON c.object_id = ic.object_id
            AND c.column_id = ic.column_id
WHERE
    i.name IS NOT NULL
    AND i.is_hypothetical = 0
    AND OBJECTPROPERTY(v.object_id, 'IsSchemaBound') = 1
