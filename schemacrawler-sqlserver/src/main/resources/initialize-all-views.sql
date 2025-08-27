-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectViewMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectViewMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectViewMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllViewMetadata') IS NOT NULL
        DROP TABLE ##AllViewMetadata;

    CREATE TABLE ##AllViewMetadata (
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        CHECK_OPTION NVARCHAR(20),
        IS_UPDATABLE NVARCHAR(5),
        VIEW_DEFINITION NVARCHAR(MAX)
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
        INSERT INTO ##AllViewMetadata
        SELECT
            V.TABLE_CATALOG,
            V.TABLE_SCHEMA,
            V.TABLE_NAME,
            V.CHECK_OPTION,
            V.IS_UPDATABLE,
            V.VIEW_DEFINITION
        FROM
            ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.VIEWS V;';

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

    SELECT * FROM ##AllViewMetadata;
END;
@
