-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectTableColumns') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectTableColumns;
@

CREATE PROCEDURE #schcrwlr_CollectTableColumns
AS
BEGIN
    SET NOCOUNT ON;

    -- Drop the global temp table if it exists
    IF OBJECT_ID('tempdb..##AllTableColumns') IS NOT NULL
        DROP TABLE ##AllTableColumns;

    -- Create the global temp table for collecting column metadata
    CREATE TABLE ##AllTableColumns (
        TABLE_CAT SYSNAME,
        TABLE_SCHEM SYSNAME,
        TABLE_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        ORDINAL_POSITION INT,
        IS_NULLABLE VARCHAR(3),
        TYPE_NAME SYSNAME,
        COLUMN_SIZE INT,
        DECIMAL_DIGITS INT,
        CHAR_OCTET_LENGTH INT,
        IS_AUTOINCREMENT VARCHAR(3),
        IS_GENERATEDCOLUMN VARCHAR(3),
        REMARKS SYSNAME,
        DATA_TYPE INT
    );

    -- Execute against each non-system database
    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllTableColumns
        SELECT
            CAST(cu.TABLE_CATALOG AS SYSNAME) AS TABLE_CAT,
            CAST(cu.TABLE_SCHEMA AS SYSNAME) AS TABLE_SCHEM,
            CAST(cu.TABLE_NAME AS SYSNAME) AS TABLE_NAME,
            CAST(cu.COLUMN_NAME AS SYSNAME) AS COLUMN_NAME,
            CAST(cu.ORDINAL_POSITION AS INTEGER) AS ORDINAL_POSITION,
            CAST(cu.IS_NULLABLE AS VARCHAR(3)) AS IS_NULLABLE,
            CAST(cu.DATA_TYPE AS SYSNAME) AS TYPE_NAME,
            CAST(COALESCE(cu.CHARACTER_MAXIMUM_LENGTH, cu.NUMERIC_PRECISION) AS INTEGER) AS COLUMN_SIZE,
            CAST(cu.NUMERIC_SCALE AS INTEGER) AS DECIMAL_DIGITS,
            CAST(cu.CHARACTER_OCTET_LENGTH AS INTEGER) AS CHAR_OCTET_LENGTH,
            CAST(CASE WHEN sc.is_identity = 1 THEN ''YES'' ELSE ''NO'' END AS VARCHAR(3)) AS IS_AUTOINCREMENT,
            CAST(CASE WHEN sc.is_computed = 1 THEN ''YES'' ELSE ''NO'' END AS VARCHAR(3)) AS IS_GENERATEDCOLUMN,
            CAST(NULL AS SYSNAME) AS REMARKS,
            CAST(
                CASE cu.DATA_TYPE
                    WHEN ''int'' THEN 4
                    WHEN ''bigint'' THEN -5
                    WHEN ''smallint'' THEN 5
                    WHEN ''tinyint'' THEN -6
                    WHEN ''bit'' THEN -7
                    WHEN ''decimal'' THEN 3
                    WHEN ''numeric'' THEN 2
                    WHEN ''float'' THEN 6
                    WHEN ''real'' THEN 7
                    WHEN ''char'' THEN 1
                    WHEN ''varchar'' THEN 12
                    WHEN ''nchar'' THEN -15
                    WHEN ''nvarchar'' THEN -9
                    WHEN ''text'' THEN -1
                    WHEN ''ntext'' THEN -16
                    WHEN ''date'' THEN 91
                    WHEN ''datetime'' THEN 93
                    WHEN ''datetime2'' THEN 93
                    WHEN ''smalldatetime'' THEN 93
                    WHEN ''time'' THEN 92
                    WHEN ''binary'' THEN -2
                    WHEN ''varbinary'' THEN -3
                    WHEN ''image'' THEN -4
                    ELSE 1111
                END AS INTEGER
            ) AS DATA_TYPE
        FROM [?].INFORMATION_SCHEMA.COLUMNS cu
        INNER JOIN [?].sys.schemas ss
            ON ss.name = cu.TABLE_SCHEMA
        INNER JOIN [?].sys.tables st
            ON st.name = cu.TABLE_NAME AND st.schema_id = ss.schema_id
        INNER JOIN [?].sys.columns sc
            ON sc.object_id = st.object_id AND sc.name = cu.COLUMN_NAME;
    END';

    -- Return the combined results
    SELECT * FROM ##AllTableColumns;
END;
@
