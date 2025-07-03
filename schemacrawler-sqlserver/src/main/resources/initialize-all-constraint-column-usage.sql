IF OBJECT_ID('tempdb..#schcrwlr_CollectConstraintColumnUsage') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectConstraintColumnUsage;
@

CREATE PROCEDURE #schcrwlr_CollectConstraintColumnUsage
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllConstraintColumnUsage') IS NOT NULL
        DROP TABLE ##AllConstraintColumnUsage;

    CREATE TABLE ##AllConstraintColumnUsage (
        CONSTRAINT_CATALOG SYSNAME,
        CONSTRAINT_SCHEMA SYSNAME,
        CONSTRAINT_NAME SYSNAME,
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        COLUMN_NAME SYSNAME,
        ORDINAL_POSTION INT
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllConstraintColumnUsage
        SELECT
            CONSTRAINT_CATALOG,
            CONSTRAINT_SCHEMA,
            CONSTRAINT_NAME,
            TABLE_CATALOG,
            TABLE_SCHEMA,
            TABLE_NAME,
            COLUMN_NAME,
            0 AS ORDINAL_POSTION
        FROM 
            [?].INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE;
    END';

    SELECT * FROM ##AllConstraintColumnUsage;
END;
@
