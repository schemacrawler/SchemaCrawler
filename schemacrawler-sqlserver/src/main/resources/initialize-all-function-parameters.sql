-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectFunctionParameters') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectFunctionParameters;
@

CREATE PROCEDURE #schcrwlr_CollectFunctionParameters
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..#AllFunctionParameters') IS NOT NULL
        DROP TABLE #AllFunctionParameters;

    CREATE TABLE #AllFunctionParameters (
        FUNCTION_CAT SYSNAME,
        FUNCTION_SCHEM SYSNAME,
        FUNCTION_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        COLUMN_TYPE INT,
        TYPE_NAME NVARCHAR(128),
        LENGTH INT,
        PRECISION INT,
        SCALE INT,
        ORDINAL_POSITION INT,
        DATA_TYPE INT,
        REMARKS NVARCHAR(128) NULL
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
        INSERT INTO #AllFunctionParameters
        SELECT
            R.ROUTINE_CATALOG AS FUNCTION_CAT,
            R.ROUTINE_SCHEMA AS FUNCTION_SCHEM,
            R.ROUTINE_NAME AS FUNCTION_NAME,
            R.SPECIFIC_NAME,
            CASE DATALENGTH(P.PARAMETER_NAME)
                WHEN 0 THEN ''@RETURN_VALUE''
                ELSE P.PARAMETER_NAME
            END
                AS COLUMN_NAME,
            CASE
                WHEN P.PARAMETER_MODE = ''IN'' THEN 1
                WHEN P.PARAMETER_MODE = ''OUT'' THEN 4
                WHEN P.PARAMETER_MODE = ''INOUT'' THEN 2
                ELSE 0
            END
              AS COLUMN_TYPE,
            P.DATA_TYPE AS TYPE_NAME,
            P.CHARACTER_MAXIMUM_LENGTH AS LENGTH,
            P.NUMERIC_PRECISION AS PRECISION,
            P.NUMERIC_SCALE,
            P.ORDINAL_POSITION,
            -1 AS DATA_TYPE,
            NULL AS REMARKS
        FROM
            ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.PARAMETERS P
            INNER JOIN ' + QUOTENAME(@dbName) + '.INFORMATION_SCHEMA.ROUTINES R
                ON P.SPECIFIC_NAME = R.SPECIFIC_NAME
                    AND P.SPECIFIC_SCHEMA = R.SPECIFIC_SCHEMA
        WHERE
            R.ROUTINE_TYPE = ''FUNCTION'';';

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

    SELECT * FROM #AllFunctionParameters;
END;
@
