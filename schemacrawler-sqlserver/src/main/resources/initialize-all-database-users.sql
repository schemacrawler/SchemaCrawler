-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

IF OBJECT_ID('tempdb..#schcrwlr_CollectDatabaseUsers') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectDatabaseUsers;
@

CREATE PROCEDURE #schcrwlr_CollectDatabaseUsers
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllDatabaseUsers') IS NOT NULL
        DROP TABLE ##AllDatabaseUsers;

    CREATE TABLE ##AllDatabaseUsers (
        USERNAME SYSNAME,
        CREATE_DATE DATETIME,
        MODIFY_DATE DATETIME,
        TYPE NVARCHAR(50),
        AUTHENTICATION_TYPE NVARCHAR(50),
        DATABASE_NAME SYSNAME
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'', ''model'', ''msdb'', ''tempdb'')
    BEGIN
        INSERT INTO ##AllDatabaseUsers
        SELECT
            NAME AS USERNAME,
            CREATE_DATE,
            MODIFY_DATE,
            TYPE_DESC AS TYPE,
            AUTHENTICATION_TYPE_DESC AS AUTHENTICATION_TYPE,
            ''?'' AS DATABASE_NAME
        FROM
            [?].SYS.DATABASE_PRINCIPALS
        WHERE
            TYPE IN (''S'', ''U'')
            AND SID IS NOT NULL
            AND NAME NOT IN (''dbo'', ''guest'', ''INFORMATION_SCHEMA'', ''sys'')
            AND principal_id > 4 -- Excludes fixed system roles and internal users
        ;
    END';

    SELECT * FROM ##AllDatabaseUsers;
END;
@
