-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectSequenceMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectSequenceMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectSequenceMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllSequenceMetadata') IS NOT NULL
        DROP TABLE #AllSequenceMetadata;

    CREATE TABLE #AllSequenceMetadata (
        SEQUENCE_CATALOG SYSNAME,
        SEQUENCE_SCHEMA SYSNAME,
        SEQUENCE_NAME SYSNAME,
        DATA_TYPE NVARCHAR(128),
        NUMERIC_PRECISION INT,
        NUMERIC_PRECISION_RADIX INT,
        NUMERIC_SCALE INT,
        START_VALUE BIGINT,
        MINIMUM_VALUE BIGINT,
        MAXIMUM_VALUE BIGINT,
        INCREMENT BIGINT,
        CYCLE_OPTION NVARCHAR(3),
        DECLARED_DATA_TYPE NVARCHAR(128),
        DECLARED_NUMERIC_PRECISION INT,
        DECLARED_NUMERIC_SCALE INT
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
        INSERT INTO #AllSequenceMetadata
        SELECT
            SEQUENCE_CATALOG,
            SEQUENCE_SCHEMA,
            SEQUENCE_NAME,
            DATA_TYPE,
            NUMERIC_PRECISION,
            NUMERIC_PRECISION_RADIX,
            NUMERIC_SCALE,
            CAST(START_VALUE AS BIGINT),
            CAST(MINIMUM_VALUE AS BIGINT),
            CAST(MAXIMUM_VALUE AS BIGINT),
            CAST(INCREMENT AS BIGINT),
            CYCLE_OPTION,
            DECLARED_DATA_TYPE,
            DECLARED_NUMERIC_PRECISION,
            DECLARED_NUMERIC_SCALE
        FROM
            INFORMATION_SCHEMA.SEQUENCES;';

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

    SELECT * FROM #AllSequenceMetadata;
END;
@
