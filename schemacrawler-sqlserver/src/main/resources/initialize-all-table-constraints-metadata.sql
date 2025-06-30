IF OBJECT_ID('tempdb..#schcrwlr_CollectTableConstraintMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectTableConstraintMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectTableConstraintMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllTableConstraintMetadata') IS NOT NULL
        DROP TABLE ##AllTableConstraintMetadata;

    CREATE TABLE ##AllTableConstraintMetadata (
        CONSTRAINT_CATALOG SYSNAME,
        CONSTRAINT_SCHEMA SYSNAME,
        CONSTRAINT_NAME SYSNAME,
        TABLE_CATALOG SYSNAME NULL,
        TABLE_SCHEMA SYSNAME NULL,
        TABLE_NAME SYSNAME,
        CONSTRAINT_TYPE NVARCHAR(20),
        IS_DEFERRABLE NVARCHAR(3),
        INITIALLY_DEFERRED NVARCHAR(3)
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllTableConstraintMetadata
        SELECT
          CONSTRAINT_CATALOG,
          CONSTRAINT_SCHEMA,
          CONSTRAINT_NAME,
          TABLE_CATALOG,
          TABLE_SCHEMA,
          TABLE_NAME,
          CONSTRAINT_TYPE,
          IS_DEFERRABLE,
          INITIALLY_DEFERRED
        FROM 
          [?].INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE
          TABLE_NAME IS NOT NULL;
    END';

    SELECT * FROM ##AllTableConstraintMetadata;
END;
@
