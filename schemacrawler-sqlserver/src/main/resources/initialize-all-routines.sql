-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectRoutineMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectRoutineMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectRoutineMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllRoutineMetadata') IS NOT NULL
        DROP TABLE ##AllRoutineMetadata;

    CREATE TABLE ##AllRoutineMetadata (
        ROUTINE_CATALOG SYSNAME,
        ROUTINE_SCHEMA SYSNAME,
        ROUTINE_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME NULL,
        ROUTINE_BODY NVARCHAR(MAX),
        ROUTINE_DEFINITION NVARCHAR(MAX)
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
        INSERT INTO ##AllRoutineMetadata
        SELECT
            R.ROUTINE_CATALOG,
            R.ROUTINE_SCHEMA,
            R.ROUTINE_NAME,
            R.SPECIFIC_NAME,
            R.ROUTINE_BODY,
            R.ROUTINE_DEFINITION
        FROM
            ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.ROUTINES R;';

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

    SELECT * FROM ##AllRoutineMetadata;
END;
@
