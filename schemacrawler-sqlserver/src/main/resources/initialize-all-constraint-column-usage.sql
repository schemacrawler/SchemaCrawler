-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectConstraintColumnUsage') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectConstraintColumnUsage;
@

CREATE PROCEDURE #schcrwlr_CollectConstraintColumnUsage
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllConstraintColumnUsage') IS NOT NULL
        DROP TABLE #AllConstraintColumnUsage;

    CREATE TABLE #AllConstraintColumnUsage (
        CONSTRAINT_CATALOG SYSNAME,
        CONSTRAINT_SCHEMA SYSNAME,
        CONSTRAINT_NAME SYSNAME,
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        ORDINAL_POSTION INT
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
        INSERT INTO #AllConstraintColumnUsage
        SELECT
            CONSTRAINT_CATALOG,
            CONSTRAINT_SCHEMA,
            CONSTRAINT_NAME,
            TABLE_CATALOG,
            TABLE_SCHEMA,
            TABLE_NAME,
            COLUMN_NAME,
            0 AS ORDINAL_POSTION
        FROM 
            ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE;';

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

    SELECT * FROM #AllConstraintColumnUsage;
END;
@
