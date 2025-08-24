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
        ROUTINE_BODY NVARCHAR(MAX),
        ROUTINE_DEFINITION NVARCHAR(MAX)
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
            R.SPECIFIC_NAME,
            R.ROUTINE_BODY,
            R.ROUTINE_DEFINITION
        FROM 
            [?].INFORMATION_SCHEMA.ROUTINES R
    END';
    
    -- Return the combined results
    SELECT * FROM ##AllRoutineMetadata;
END;
@
