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
            ''?'' AS ROUTINE_CATALOG,
            OBJECT_SCHEMA_NAME(d.referencing_id, DB_ID(''?'')) AS ROUTINE_SCHEMA,
            OBJECT_NAME(d.referencing_id, DB_ID(''?'')) AS ROUTINE_NAME,
            NULL AS SPECIFIC_NAME,
            ''?'' AS REFERENCED_OBJECT_CATALOG,
            OBJECT_SCHEMA_NAME(o.object_id, DB_ID(''?'')) AS REFERENCED_OBJECT_SCHEMA,
            o.name AS REFERENCED_OBJECT_NAME,
            NULL AS REFERENCED_OBJECT_SPECIFIC_NAME,
            o.type_desc AS REFERENCED_OBJECT_TYPE
        FROM 
        	[?].sys.sql_expression_dependencies AS d
        	INNER JOIN [?].sys.objects AS o 
        		ON d.referenced_id = o.object_id
        	INNER JOIN [?].sys.all_objects AS ao 
        		ON d.referencing_id = ao.object_id
        WHERE OBJECTPROPERTY(d.referencing_id, ''IsProcedure'') = 1
          AND ao.IS_MS_SHIPPED = 0;
    END';

    -- Return the combined results
    SELECT * FROM ##ProcedureReferences;
END;
@