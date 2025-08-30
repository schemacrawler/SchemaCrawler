-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectDatabaseUsers') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectDatabaseUsers;
@

CREATE PROCEDURE #schcrwlr_CollectDatabaseUsers
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllDatabaseUsers') IS NOT NULL
        DROP TABLE #AllDatabaseUsers;

    CREATE TABLE #AllDatabaseUsers (
        USERNAME SYSNAME,
        CREATE_DATE DATETIME,
        MODIFY_DATE DATETIME,
        TYPE NVARCHAR(50),
        AUTHENTICATION_TYPE NVARCHAR(50),
        DATABASE_NAME SYSNAME
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
        INSERT INTO #AllDatabaseUsers
        SELECT
            NAME AS USERNAME,
            CREATE_DATE,
            MODIFY_DATE,
            TYPE_DESC AS TYPE,
            AUTHENTICATION_TYPE_DESC AS AUTHENTICATION_TYPE,
            ''' + @dbName + ''' AS DATABASE_NAME
        FROM ' + QUOTENAME(@dbName) + '.SYS.DATABASE_PRINCIPALS
        WHERE
            TYPE IN (''S'', ''U'')
            AND SID IS NOT NULL
            AND NAME NOT IN (''dbo'', ''guest'', ''INFORMATION_SCHEMA'', ''sys'')
            AND principal_id > 4;';

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

    SELECT * FROM #AllDatabaseUsers;
END;
@
