-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectViewTableUsage') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectViewTableUsage;
@

CREATE PROCEDURE #schcrwlr_CollectViewTableUsage
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllViewTableUsage') IS NOT NULL
        DROP TABLE #AllViewTableUsage;

    CREATE TABLE #AllViewTableUsage (
        VIEW_CATALOG SYSNAME,
        VIEW_SCHEMA SYSNAME,
        VIEW_NAME SYSNAME,
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME
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
        INSERT INTO #AllViewTableUsage
        SELECT
            ''' + @dbName + '''
                AS VIEW_CATALOG,
            vs.name
                AS VIEW_SCHEMA,
            v.name
                AS VIEW_NAME,
            COALESCE(sed.referenced_database_name, ''' + @dbName + ''')
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
        ;';

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

    SELECT * FROM #AllViewTableUsage;
END;
@
