-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectStoredProcedures') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectStoredProcedures;
@

CREATE PROCEDURE #schcrwlr_CollectStoredProcedures
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllStoredProcedures') IS NOT NULL
        DROP TABLE #AllStoredProcedures;

    CREATE TABLE #AllStoredProcedures (
        PROCEDURE_CAT SYSNAME,
        PROCEDURE_SCHEM SYSNAME,
        PROCEDURE_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME,
        PROCEDURE_TYPE INTEGER NULL,
        REMARKS NVARCHAR(255) NULL,
        IS_DETERMINISTIC NVARCHAR(5),
        SQL_DATA_ACCESS NVARCHAR(30),
        IS_NULL_CALL NVARCHAR(5),
        MAX_DYNAMIC_RESULT_SETS INT,
        CREATED DATETIME,
        LAST_ALTERED DATETIME
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
        INSERT INTO #AllStoredProcedures
        SELECT
            R.ROUTINE_CATALOG AS PROCEDURE_CAT,
            R.ROUTINE_SCHEMA AS PROCEDURE_SCHEM,
            R.ROUTINE_NAME AS PROCEDURE_NAME,
            R.SPECIFIC_NAME,
            1 AS PROCEDURE_TYPE,
            NULL AS REMARKS,
            R.IS_DETERMINISTIC,
            R.SQL_DATA_ACCESS,
            R.IS_NULL_CALL,
            R.MAX_DYNAMIC_RESULT_SETS,
            R.CREATED,
            R.LAST_ALTERED
        FROM
            INFORMATION_SCHEMA.ROUTINES R
        WHERE
            R.ROUTINE_TYPE = ''PROCEDURE'';';

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

    SELECT * FROM #AllStoredProcedures;
END;
@
