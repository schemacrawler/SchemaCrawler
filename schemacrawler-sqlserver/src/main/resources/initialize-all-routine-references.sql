-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectProcedureReferences') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectProcedureReferences;
@

CREATE PROCEDURE #schcrwlr_CollectProcedureReferences
AS
BEGIN
    SET NOCOUNT ON;

    -- Drop the global temp table if it exists
    IF OBJECT_ID('tempdb..##ProcedureReferences') IS NOT NULL
        DROP TABLE ##ProcedureReferences;

    -- Create the global temp table for collecting procedure references
    CREATE TABLE ##ProcedureReferences (
        ROUTINE_CATALOG SYSNAME,
        ROUTINE_SCHEMA SYSNAME,
        ROUTINE_NAME SYSNAME,
        SPECIFIC_NAME SYSNAME NULL,
        REFERENCED_OBJECT_CATALOG SYSNAME NULL,
        REFERENCED_OBJECT_SCHEMA SYSNAME NULL,
        REFERENCED_OBJECT_NAME SYSNAME NULL,
        REFERENCED_OBJECT_SPECIFIC_NAME SYSNAME NULL,
        REFERENCED_OBJECT_TYPE NVARCHAR(60) NULL
    );

    -- Execute against each non-system database
    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##ProcedureReferences
        SELECT
            R.ROUTINE_CATALOG,
            R.ROUTINE_SCHEMA,
            R.ROUTINE_NAME,
            R.SPECIFIC_NAME,
            ''?'' AS REFERENCED_OBJECT_CATALOG,
            OBJECT_SCHEMA_NAME(d.referenced_id, DB_ID(R.ROUTINE_CATALOG)) AS REFERENCED_OBJECT_SCHEMA,
            o.name AS REFERENCED_OBJECT_NAME,
            o.type_desc AS REFERENCED_OBJECT_TYPE
        FROM [?].INFORMATION_SCHEMA.ROUTINES R
        INNER JOIN [?].sys.sql_expression_dependencies d
            ON OBJECT_ID(R.ROUTINE_SCHEMA + ''.'' + R.ROUTINE_NAME) = d.referencing_id
        INNER JOIN [?].sys.objects o
            ON d.referenced_id = o.object_id;
    END';

    -- Return the combined results
    SELECT * FROM ##ProcedureReferences;
END;
@