-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectSynonymMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectSynonymMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectSynonymMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllSynonymMetadata') IS NOT NULL
        DROP TABLE #AllSynonymMetadata;

    CREATE TABLE #AllSynonymMetadata (
        SYNONYM_CATALOG SYSNAME,
        SYNONYM_SCHEMA SYSNAME,
        SYNONYM_NAME SYSNAME,
        REFERENCED_OBJECT_SERVER SYSNAME NULL,
        REFERENCED_OBJECT_CATALOG SYSNAME NULL,
        REFERENCED_OBJECT_SCHEMA SYSNAME NULL,
        REFERENCED_OBJECT_NAME SYSNAME
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
        INSERT INTO #AllSynonymMetadata
        SELECT
            ''' + @dbName + ''' AS SYNONYM_CATALOG,
            SCHEMA_NAME(SCHEMA_ID) AS SYNONYM_SCHEMA,
            NAME AS SYNONYM_NAME,
            PARSENAME(BASE_OBJECT_NAME, 4) AS REFERENCED_OBJECT_SERVER,
            PARSENAME(BASE_OBJECT_NAME, 3) AS REFERENCED_OBJECT_CATALOG,
            PARSENAME(BASE_OBJECT_NAME, 2) AS REFERENCED_OBJECT_SCHEMA,
            PARSENAME(BASE_OBJECT_NAME, 1) AS REFERENCED_OBJECT_NAME
        FROM
            SYS.SYNONYMS;';

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

    SELECT * FROM #AllSynonymMetadata;
END;
@

