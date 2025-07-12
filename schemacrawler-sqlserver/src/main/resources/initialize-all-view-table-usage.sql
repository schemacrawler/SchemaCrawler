-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectViewTableUsage') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectViewTableUsage;
@

CREATE PROCEDURE #schcrwlr_CollectViewTableUsage
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllViewTableUsage') IS NOT NULL
        DROP TABLE ##AllViewTableUsage;

    CREATE TABLE ##AllViewTableUsage (
        VIEW_CATALOG SYSNAME,
        VIEW_SCHEMA SYSNAME,
        VIEW_NAME SYSNAME,
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllViewTableUsage
        SELECT
            VIEW_CATALOG,
            VIEW_SCHEMA,
            VIEW_NAME,
            TABLE_CATALOG,
            TABLE_SCHEMA,
            TABLE_NAME
        FROM 
            [?].INFORMATION_SCHEMA.VIEW_TABLE_USAGE;
    END';

    SELECT * FROM ##AllViewTableUsage;
END;
@
