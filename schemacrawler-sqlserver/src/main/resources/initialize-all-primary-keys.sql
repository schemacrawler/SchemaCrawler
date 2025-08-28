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

    IF OBJECT_ID('tempdb..##AllPrimaryKeyMetadata') IS NOT NULL
        DROP TABLE ##AllPrimaryKeyMetadata;

    CREATE TABLE ##AllPrimaryKeyMetadata (
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
        SELECT
            ''' + @dbName + ''' AS TABLE_CAT,
            CAST(tc.TABLE_SCHEMA AS SYSNAME) AS TABLE_SCHEM,
            CAST(tc.TABLE_NAME AS SYSNAME) AS TABLE_NAME,
            CAST(tc.CONSTRAINT_NAME AS SYSNAME) AS PK_NAME,
            CAST(kcu.COLUMN_NAME AS SYSNAME) AS COLUMN_NAME,
            CAST(kcu.ORDINAL_POSITION AS SMALLINT) AS KEY_SEQ
        FROM
            ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
            INNER JOIN ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
                ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
                    AND tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA
                    AND tc.TABLE_NAME = kcu.TABLE_NAME
        WHERE
            tc.CONSTRAINT_TYPE = ''PRIMARY KEY'';';

        BEGIN TRY
            INSERT INTO ##AllPrimaryKeyMetadata
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

    SELECT * FROM ##AllPrimaryKeyMetadata;
END;
@
