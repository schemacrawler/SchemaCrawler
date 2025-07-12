-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectAdditionalColumnAttributes') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectAdditionalColumnAttributes;
@

CREATE PROCEDURE #schcrwlr_CollectAdditionalColumnAttributes
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllAdditionalColumnAttributes') IS NOT NULL
        DROP TABLE ##AllAdditionalColumnAttributes;

    CREATE TABLE ##AllAdditionalColumnAttributes (
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        REMARKS NVARCHAR(MAX)
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'', ''model'', ''msdb'', ''tempdb'')
    BEGIN
        INSERT INTO ##AllAdditionalColumnAttributes
        SELECT
            DB_NAME() AS TABLE_CATALOG,
            SCHEMA_NAME(O.SCHEMA_ID) AS TABLE_SCHEMA,
            O.NAME AS TABLE_NAME,
            C.NAME AS COLUMN_NAME,
            CONVERT(NVARCHAR(MAX), EP.VALUE) AS REMARKS
        FROM 
            [?].SYS.COLUMNS AS C
            INNER JOIN [?].SYS.ALL_OBJECTS O
                ON C.OBJECT_ID = O.OBJECT_ID
            INNER JOIN [?].SYS.EXTENDED_PROPERTIES EP
                ON EP.MAJOR_ID = C.OBJECT_ID AND EP.MINOR_ID = C.COLUMN_ID
        WHERE
            O.IS_MS_SHIPPED != 1
            AND O.TYPE = ''U''
            AND EP.CLASS = 1
            AND EP.NAME = ''MS_Description'';
    END';

    SELECT * FROM ##AllAdditionalColumnAttributes;
END;
@
