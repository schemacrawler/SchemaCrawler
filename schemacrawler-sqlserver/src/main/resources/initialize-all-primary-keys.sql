-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectPrimaryKeyMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectPrimaryKeyMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectPrimaryKeyMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllPrimaryKeyMetadata') IS NOT NULL
        DROP TABLE #AllPrimaryKeyMetadata;

    CREATE TABLE #AllPrimaryKeyMetadata (
        TABLE_CAT SYSNAME,
        TABLE_SCHEM SYSNAME,
        TABLE_NAME SYSNAME,
        PK_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        KEY_SEQ SMALLINT
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
        USE ' + QUOTENAME(@dbName) + ';
        SELECT
            ''' + @dbName + ''' AS TABLE_CAT,
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
            kc.type = ''PK''
            AND i.name IS NOT NULL
            AND i.is_hypothetical = 0;';

        BEGIN TRY
            INSERT INTO #AllPrimaryKeyMetadata
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

    SELECT * FROM #AllPrimaryKeyMetadata;
END;
@
