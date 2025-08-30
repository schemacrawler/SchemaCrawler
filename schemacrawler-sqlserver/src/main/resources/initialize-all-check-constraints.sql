-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectCheckConstraintMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectCheckConstraintMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectCheckConstraintMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllCheckConstraintMetadata') IS NOT NULL
        DROP TABLE #AllCheckConstraintMetadata;

    CREATE TABLE #AllCheckConstraintMetadata (
        CONSTRAINT_CATALOG SYSNAME,
        CONSTRAINT_SCHEMA SYSNAME,
        CONSTRAINT_NAME SYSNAME,
        CHECK_CLAUSE NVARCHAR(MAX)
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
        INSERT INTO #AllCheckConstraintMetadata
        SELECT
            ''' + @dbName + ''' AS CONSTRAINT_CATALOG,
            CONSTRAINT_SCHEMA,
            CONSTRAINT_NAME,
            CHECK_CLAUSE
        FROM
            INFORMATION_SCHEMA.CHECK_CONSTRAINTS;';

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

    SELECT * FROM #AllCheckConstraintMetadata;
END;
@
