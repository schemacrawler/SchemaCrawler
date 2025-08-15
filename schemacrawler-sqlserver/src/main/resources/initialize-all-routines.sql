-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectRoutineMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectRoutineMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectRoutineMetadata
AS
BEGIN
    SET NOCOUNT ON;

    -- Drop the global temp table if it exists
    IF OBJECT_ID('tempdb..##AllRoutineMetadata') IS NOT NULL
        DROP TABLE ##AllRoutineMetadata;

    -- Create the global temp table for collecting routine metadata
    CREATE TABLE ##AllRoutineMetadata (
        ROUTINE_CATALOG SYSNAME,
        ROUTINE_SCHEMA SYSNAME,
        ROUTINE_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME NULL,
        ROUTINE_TYPE NVARCHAR(20),
        DATA_TYPE NVARCHAR(128),
        CHARACTER_MAXIMUM_LENGTH INT,
        CHARACTER_OCTET_LENGTH INT,
        NUMERIC_PRECISION INT,
        NUMERIC_PRECISION_RADIX INT,
        NUMERIC_SCALE INT,
        DATETIME_PRECISION INT,
        ROUTINE_BODY NVARCHAR(MAX),
        ROUTINE_DEFINITION NVARCHAR(MAX),
        IS_DETERMINISTIC NVARCHAR(5),
        SQL_DATA_ACCESS NVARCHAR(30),
        IS_NULL_CALL NVARCHAR(5),
        MAX_DYNAMIC_RESULT_SETS INT,
        CREATED DATETIME,
        LAST_ALTERED DATETIME,
        OBJECT_ID INT
    );

    -- Execute against each non-system database
    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllRoutineMetadata
        SELECT 
            R.ROUTINE_CATALOG,
            R.ROUTINE_SCHEMA,
            R.ROUTINE_NAME,
            NULL AS SPECIFIC_NAME,
            R.ROUTINE_TYPE,
            R.DATA_TYPE,
            R.CHARACTER_MAXIMUM_LENGTH,
            R.CHARACTER_OCTET_LENGTH,
            R.NUMERIC_PRECISION,
            R.NUMERIC_PRECISION_RADIX,
            R.NUMERIC_SCALE,
            R.DATETIME_PRECISION,
            R.ROUTINE_BODY,
            OBJECT_DEFINITION(OBJECT_ID(R.ROUTINE_CATALOG + ''.'' + R.ROUTINE_SCHEMA + ''.'' + R.ROUTINE_NAME)),
            R.IS_DETERMINISTIC,
            R.SQL_DATA_ACCESS,
            R.IS_NULL_CALL,
            R.MAX_DYNAMIC_RESULT_SETS,
            R.CREATED,
            R.LAST_ALTERED,
            AO.OBJECT_ID
        FROM 
            [?].INFORMATION_SCHEMA.ROUTINES R
            INNER JOIN [?].SYS.ALL_OBJECTS AO
                ON OBJECT_ID(R.ROUTINE_CATALOG + ''.'' + R.ROUTINE_SCHEMA + ''.'' + R.ROUTINE_NAME) = AO.OBJECT_ID
        WHERE 
            AO.IS_MS_SHIPPED = 0;
    END';
    
    -- Return the combined results
    SELECT * FROM ##AllRoutineMetadata;
END;
@
