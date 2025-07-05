IF OBJECT_ID('tempdb..#schcrwlr_CollectCheckConstraintMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectCheckConstraintMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectCheckConstraintMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllCheckConstraintMetadata') IS NOT NULL
        DROP TABLE ##AllCheckConstraintMetadata;

    CREATE TABLE ##AllCheckConstraintMetadata (
        CONSTRAINT_CATALOG SYSNAME,
        CONSTRAINT_SCHEMA SYSNAME,
        CONSTRAINT_NAME SYSNAME,
        CHECK_CLAUSE NVARCHAR(MAX)
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllCheckConstraintMetadata
        SELECT
            CONSTRAINT_CATALOG,
            CONSTRAINT_SCHEMA,
            CONSTRAINT_NAME,
            CHECK_CLAUSE
        FROM 
            [?].INFORMATION_SCHEMA.CHECK_CONSTRAINTS;
    END';

    SELECT * FROM ##AllCheckConstraintMetadata;
END;
@
