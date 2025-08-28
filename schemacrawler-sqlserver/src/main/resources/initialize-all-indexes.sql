-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectIndexMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectIndexMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectIndexMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllIndexMetadata') IS NOT NULL
        DROP TABLE ##AllIndexMetadata;

    CREATE TABLE ##AllIndexMetadata (
        TABLE_CAT SYSNAME,
        TABLE_SCHEM SYSNAME,
        TABLE_NAME SYSNAME,
        NON_UNIQUE SMALLINT,
        INDEX_QUALIFIER SYSNAME NULL,
        INDEX_NAME SYSNAME,
        TYPE SMALLINT,
        ORDINAL_POSITION SMALLINT,
        COLUMN_NAME SYSNAME,
        ASC_OR_DESC VARCHAR(1),
        CARDINALITY INT NULL,
        PAGES INT NULL,
        FILTER_CONDITION SYSNAME NULL,
        is_primary_key BIT,
        is_unique_constraint BIT,
        is_hypothetical BIT,
        auto_created BIT,
        type_desc NVARCHAR(60)
    );

    DECLARE @dbName SYSNAME;
    DECLARE @sql NVARCHAR(MAX);

    DECLARE db_cursor CURSOR FOR
        SELECT name
        FROM sys.databases
        WHERE name NOT IN ('master', 'model', 'msdb', 'tempdb')
          AND state_desc = 'ONLINE';

    OPEN db_cursor;
    FETCH NEXT FROM db_cursor INTO @dbName;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        SET @sql = N'
        SELECT
            ''' + @dbName + ''' AS TABLE_CAT,
            CAST(t.TABLE_SCHEMA AS SYSNAME) AS TABLE_SCHEM,
            CAST(t.TABLE_NAME AS SYSNAME) AS TABLE_NAME,
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
            CAST(CASE WHEN ic.is_descending_key = 1 THEN ''D'' ELSE ''A'' END AS VARCHAR(1)) AS ASC_OR_DESC,
            CAST(NULL AS INT) AS CARDINALITY,
            CAST(NULL AS INT) AS PAGES,
            CAST(i.filter_definition AS SYSNAME) AS FILTER_CONDITION,
            i.is_primary_key,
            i.is_unique_constraint,
            i.is_hypothetical,
            i.auto_created,
            i.type_desc
        FROM
            ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.TABLES t
            INNER JOIN ' + QUOTENAME(@dbName) + '.sys.schemas s
                ON s.name = t.TABLE_SCHEMA
            INNER JOIN ' + QUOTENAME(@dbName) + '.sys.tables st
                ON st.name = t.TABLE_NAME AND st.schema_id = s.schema_id
            INNER JOIN ' + QUOTENAME(@dbName) + '.sys.indexes i
                ON i.object_id = st.object_id
            INNER JOIN ' + QUOTENAME(@dbName) + '.sys.index_columns ic
                ON ic.object_id = i.object_id AND ic.index_id = i.index_id
            INNER JOIN ' + QUOTENAME(@dbName) + '.sys.columns c
                ON c.object_id = ic.object_id AND c.column_id = ic.column_id
        WHERE
            i.name IS NOT NULL
            AND i.is_hypothetical = 0;';

        BEGIN TRY
            INSERT INTO ##AllIndexMetadata
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

    SELECT * FROM ##AllIndexMetadata;
END;
@
