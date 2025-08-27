-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectTableConstraintMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectTableConstraintMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectTableConstraintMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllTableConstraintMetadata') IS NOT NULL
        DROP TABLE ##AllTableConstraintMetadata;

    CREATE TABLE ##AllTableConstraintMetadata (
        CONSTRAINT_CATALOG SYSNAME,
        CONSTRAINT_SCHEMA SYSNAME,
        CONSTRAINT_NAME SYSNAME,
        TABLE_CATALOG SYSNAME NULL,
        TABLE_SCHEMA SYSNAME NULL,
        TABLE_NAME SYSNAME,
        CONSTRAINT_TYPE NVARCHAR(20),
        IS_DEFERRABLE NVARCHAR(3),
        INITIALLY_DEFERRED NVARCHAR(3)
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
        INSERT INTO ##AllTableConstraintMetadata
        SELECT
            CONSTRAINT_CATALOG,
            CONSTRAINT_SCHEMA,
            CONSTRAINT_NAME,
            TABLE_CATALOG,
            TABLE_SCHEMA,
            TABLE_NAME,
            CONSTRAINT_TYPE,
            IS_DEFERRABLE,
            INITIALLY_DEFERRED
        FROM
           ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE
           TABLE_NAME IS NOT NULL;';

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

    SELECT * FROM ##AllTableConstraintMetadata;
END;
@
