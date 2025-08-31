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
    IF OBJECT_ID('tempdb..#AllTableColumns') IS NOT NULL
        DROP TABLE #AllTableColumns;

    -- Create the global temp table for collecting column metadata
    CREATE TABLE #AllTableColumns (
        TABLE_CAT SYSNAME,
        TABLE_SCHEM SYSNAME,
        TABLE_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        ORDINAL_POSITION INT,
        NULLABLE INT,
        COLUMN_DEF VARCHAR(4000),
        TYPE_NAME SYSNAME,
        COLUMN_SIZE INT,
        DECIMAL_DIGITS INT,
        CHAR_OCTET_LENGTH INT,
        IS_AUTOINCREMENT VARCHAR(3),
        IS_GENERATEDCOLUMN VARCHAR(3),
        REMARKS SYSNAME NULL,
        DATA_TYPE INT
    );

    DECLARE @dbName SYSNAME;
    DECLARE db_cursor CURSOR FOR
        SELECT name
        FROM sys.databases
        WHERE name NOT IN ('master', 'model', 'msdb', 'tempdb', 'rdsadmin')
          AND state_desc = 'ONLINE';

    OPEN db_cursor;
    FETCH NEXT FROM db_cursor INTO @dbName;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE @sql NVARCHAR(MAX) = N'
        USE ' + QUOTENAME(@dbName) + ';
        INSERT INTO #AllTableColumns
        SELECT
            CAST(cu.TABLE_CATALOG AS SYSNAME) AS TABLE_CAT,
            CAST(cu.TABLE_SCHEMA AS SYSNAME) AS TABLE_SCHEM,
            CAST(cu.TABLE_NAME AS SYSNAME) AS TABLE_NAME,
            CAST(cu.COLUMN_NAME AS SYSNAME) AS COLUMN_NAME,
            CAST(cu.ORDINAL_POSITION AS INTEGER) AS ORDINAL_POSITION,
            CASE cu.IS_NULLABLE
              WHEN ''YES'' THEN 1
              ELSE 0
            END
              AS NULLABLE,
            cu.COLUMN_DEFAULT AS COLUMN_DEF,
            CAST(
                CASE
                    WHEN ty.is_user_defined = 1
                    THEN ts.name + ''.'' + ty.name
                    ELSE ty.name
                END AS SYSNAME
            )
                AS TYPE_NAME,
            CAST(COALESCE(cu.CHARACTER_MAXIMUM_LENGTH, cu.NUMERIC_PRECISION) AS INTEGER) AS COLUMN_SIZE,
            CAST(cu.NUMERIC_SCALE AS INTEGER) AS DECIMAL_DIGITS,
            CAST(cu.CHARACTER_OCTET_LENGTH AS INTEGER) AS CHAR_OCTET_LENGTH,
            CAST(CASE WHEN sc.is_identity = 1 THEN ''YES'' ELSE ''NO'' END AS VARCHAR(3)) AS IS_AUTOINCREMENT,
            CAST(CASE WHEN sc.is_computed = 1 THEN ''YES'' ELSE ''NO'' END AS VARCHAR(3)) AS IS_GENERATEDCOLUMN,
            CAST(NULL AS SYSNAME) AS REMARKS,
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
            END AS DATA_TYPE
        FROM
            INFORMATION_SCHEMA.COLUMNS cu
            INNER JOIN sys.schemas ss
                ON ss.name = cu.TABLE_SCHEMA
            INNER JOIN sys.objects so
                ON so.name = cu.TABLE_NAME AND so.schema_id = ss.schema_id
                    AND so.type IN (''U'', ''V'') -- U = table, V = view
            INNER JOIN sys.columns sc
                ON sc.object_id = so.object_id AND sc.name = cu.COLUMN_NAME
            INNER JOIN sys.types ty
                ON sc.user_type_id = ty.user_type_id
            INNER JOIN sys.schemas ts
                ON ty.schema_id = ts.schema_id
        ';

        BEGIN TRY
            EXEC sp_executesql @sql;
        END TRY
        BEGIN CATCH
            DECLARE @error NVARCHAR(MAX) = ERROR_MESSAGE();
            RAISERROR(@error, 5, 1);
        END CATCH;

        FETCH NEXT FROM db_cursor INTO @dbName;
    END;

    CLOSE db_cursor;
    DEALLOCATE db_cursor;

    -- Return the combined results
    SELECT * FROM #AllTableColumns;
END;
@
