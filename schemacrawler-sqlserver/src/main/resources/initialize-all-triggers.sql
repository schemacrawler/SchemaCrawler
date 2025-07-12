-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectTriggerMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectTriggerMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectTriggerMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllTriggerMetadata') IS NOT NULL
        DROP TABLE ##AllTriggerMetadata;

    CREATE TABLE ##AllTriggerMetadata (
        TRIGGER_CATALOG SYSNAME,
        TRIGGER_SCHEMA SYSNAME,
        TRIGGER_NAME SYSNAME,
        EVENT_MANIPULATION NVARCHAR(100),
        EVENT_OBJECT_CATALOG SYSNAME,
        EVENT_OBJECT_SCHEMA SYSNAME,
        EVENT_OBJECT_TABLE SYSNAME,
        ACTION_STATEMENT NVARCHAR(MAX),
        ACTION_ORDER INT,
        ACTION_CONDITION NVARCHAR(100),
        ACTION_ORIENTATION NVARCHAR(20),
        CONDITION_TIMING NVARCHAR(20)
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllTriggerMetadata
        SELECT
            ist.TABLE_CATALOG,
            ist.TABLE_SCHEMA,
            tr.name,
            CONCAT(
                CASE WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsInsertTrigger'') = 1 THEN ''INSERT'' ELSE ''UNKNOWN'' END, '', '',
                CASE WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsUpdateTrigger'') = 1 THEN ''UPDATE'' ELSE ''UNKNOWN'' END, '', '',
                CASE WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsDeleteTrigger'') = 1 THEN ''DELETE'' ELSE ''UNKNOWN'' END
            ) AS EVENT_MANIPULATION,
            ist.TABLE_CATALOG,
            ist.TABLE_SCHEMA,
            ist.TABLE_NAME,
            OBJECT_DEFINITION(tr.object_id),
            CASE
                WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsInsertTrigger'') = 1 THEN OBJECTPROPERTY(tr.object_id, ''TriggerInsertOrder'')
                WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsUpdateTrigger'') = 1 THEN OBJECTPROPERTY(tr.object_id, ''TriggerUpdateOrder'')
                WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsDeleteTrigger'') = 1 THEN OBJECTPROPERTY(tr.object_id, ''TriggerDeleteOrder'')
                ELSE 1
            END AS ACTION_ORDER,
            '''',
            ''STATEMENT'',
            CASE
                WHEN OBJECTPROPERTY(tr.object_id, ''ExecIsAfterTrigger'') = 1 THEN ''AFTER''
                ELSE ''INSTEAD OF''
            END AS CONDITION_TIMING
        FROM 
            [?].sys.triggers AS tr
            INNER JOIN [?].sys.all_objects AS tbl
                ON tr.parent_id = tbl.object_id
            INNER JOIN [?].INFORMATION_SCHEMA.TABLES AS ist
                ON tbl.name = ist.TABLE_NAME AND SCHEMA_NAME(tbl.schema_id) = ist.TABLE_SCHEMA
        WHERE 
            tr.IS_MS_SHIPPED = 0
            AND tbl.IS_MS_SHIPPED = 0;
    END';

    SELECT * FROM ##AllTriggerMetadata;
END;
@
