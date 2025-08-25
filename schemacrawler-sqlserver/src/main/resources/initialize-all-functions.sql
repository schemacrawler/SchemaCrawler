-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectFunctions') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectFunctions;
@

CREATE PROCEDURE #schcrwlr_CollectFunctions
AS
BEGIN
    SET NOCOUNT ON;

    -- Drop the global temp table if it exists
    IF OBJECT_ID('tempdb..##AllFunctions') IS NOT NULL
        DROP TABLE ##AllFunctions;

    -- Create the global temp table for collecting function metadata
    CREATE TABLE ##AllFunctions (
        FUNCTION_CAT SYSNAME,
        FUNCTION_SCHEM SYSNAME,
        FUNCTION_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME,
        FUNCTION_TYPE NVARCHAR(20) NULL,
        REMARKS NVARCHAR(255) NULL,
        IS_DETERMINISTIC NVARCHAR(5),
        SQL_DATA_ACCESS NVARCHAR(30),
        IS_NULL_CALL NVARCHAR(5),
        MAX_DYNAMIC_RESULT_SETS INT,
        CREATED DATETIME,
        LAST_ALTERED DATETIME
    );

    -- Execute against each non-system database
    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllFunctions
        SELECT
            R.ROUTINE_CATALOG AS FUNCTION_CAT,
            R.ROUTINE_SCHEMA AS FUNCTION_SCHEM,
            R.ROUTINE_NAME AS FUNCTION_NAME,
            R.SPECIFIC_NAME,
            1 AS FUNCTION_TYPE,
            NULL AS REMARKS,
            R.IS_DETERMINISTIC,
            R.SQL_DATA_ACCESS,
            R.IS_NULL_CALL,
            R.MAX_DYNAMIC_RESULT_SETS,
            R.CREATED,
            R.LAST_ALTERED
        FROM 
            [?].INFORMATION_SCHEMA.ROUTINES R
        WHERE 
            R.ROUTINE_TYPE = ''FUNCTION'';
    END';

    -- Return the combined results
    SELECT * FROM ##AllFunctions;
END;
@
