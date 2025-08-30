-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectProcedureReferences') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectProcedureReferences;
@

CREATE PROCEDURE #schcrwlr_CollectProcedureReferences
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##ProcedureReferences') IS NOT NULL
        DROP TABLE ##ProcedureReferences;

    CREATE TABLE ##ProcedureReferences (
        ROUTINE_CATALOG SYSNAME,
        ROUTINE_SCHEMA SYSNAME,
        ROUTINE_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME NULL,
        REFERENCED_OBJECT_CATALOG SYSNAME NULL,
        REFERENCED_OBJECT_SCHEMA SYSNAME NULL,
        REFERENCED_OBJECT_NAME SYSNAME NULL,
        REFERENCED_OBJECT_SPECIFIC_NAME SYSNAME NULL,
        REFERENCED_OBJECT_TYPE NVARCHAR(60) NULL
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
        INSERT INTO ##ProcedureReferences
        SELECT
            R.ROUTINE_CATALOG,
            R.ROUTINE_SCHEMA,
            R.ROUTINE_NAME,
            R.SPECIFIC_NAME,
            ''' + @dbName + ''' AS REFERENCED_OBJECT_CATALOG,
            OBJECT_SCHEMA_NAME(d.referenced_id, DB_ID(''' + @dbName + ''')) AS REFERENCED_OBJECT_SCHEMA,
            o.name AS REFERENCED_OBJECT_NAME,
            o.name AS REFERENCED_OBJECT_SPECIFIC_NAME,
            o.type_desc AS REFERENCED_OBJECT_TYPE
        FROM
            INFORMATION_SCHEMA.ROUTINES R
            INNER JOIN sys.sql_expression_dependencies d
                ON OBJECT_ID(R.ROUTINE_CATALOG + ''.'' + R.ROUTINE_SCHEMA + ''.'' + R.ROUTINE_NAME) = d.referencing_id
            INNER JOIN sys.objects o
                ON d.referenced_id = o.object_id;';

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

    SELECT * FROM ##ProcedureReferences;
END;
@
