-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectSchemas') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectSchemas;
@

CREATE PROCEDURE #schcrwlr_CollectSchemas
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllSchemas') IS NOT NULL
        DROP TABLE #AllSchemas;

    CREATE TABLE #AllSchemas (
        CATALOG_NAME SYSNAME,
        SCHEMA_NAME SYSNAME
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
        INSERT INTO #AllSchemas
        SELECT
            CAST(''' + @dbName + ''' AS SYSNAME) AS CATALOG_NAME,
            CAST(s.name AS SYSNAME) AS SCHEMA_NAME
        FROM
            sys.schemas s
        WHERE
            s.name NOT IN (
                ''db_accessadmin'',
                ''db_backupoperator'',
                ''db_datareader'',
                ''db_datawriter'',
                ''db_ddladmin'',
                ''db_denydatareader'',
                ''db_denydatawriter'',
                ''db_owner'',
                ''db_securityadmin'',
                ''INFORMATION_SCHEMA'',
                ''sys''
            )
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
    SELECT * FROM #AllSchemas;
END;
@
