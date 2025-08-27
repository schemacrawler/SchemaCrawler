-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectAdditionalTableAttributes') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectAdditionalTableAttributes;
@

CREATE PROCEDURE #schcrwlr_CollectAdditionalTableAttributes
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllAdditionalTableAttributes') IS NOT NULL
        DROP TABLE ##AllAdditionalTableAttributes;

    CREATE TABLE ##AllAdditionalTableAttributes (
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        REMARKS NVARCHAR(MAX)
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
        INSERT INTO ##AllAdditionalTableAttributes
        SELECT
            ''' + @dbName + ''' AS TABLE_CATALOG,
            SCHEMA_NAME(O.SCHEMA_ID) AS TABLE_SCHEMA,
            O.NAME AS TABLE_NAME,
            CONVERT(NVARCHAR(MAX), EP.VALUE) AS REMARKS
        FROM
            ' + QUOTENAME(@dbName) + '.SYS.ALL_OBJECTS O
        INNER JOIN
            ' + QUOTENAME(@dbName) + '.SYS.EXTENDED_PROPERTIES EP
            ON O.OBJECT_ID = EP.MAJOR_ID AND EP.MINOR_ID = 0
        WHERE
            O.IS_MS_SHIPPED != 1
            AND O.TYPE = ''U''
            AND EP.CLASS = 1
            AND EP.NAME = ''MS_Description'';';

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

    SELECT * FROM ##AllAdditionalTableAttributes;
END;
@
