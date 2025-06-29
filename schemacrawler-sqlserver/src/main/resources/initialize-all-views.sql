IF OBJECT_ID('tempdb..#schcrwlr_CollectViewMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectViewMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectViewMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllViewMetadata') IS NOT NULL
        DROP TABLE ##AllViewMetadata;

    CREATE TABLE ##AllViewMetadata (
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        CHECK_OPTION NVARCHAR(20),
        IS_UPDATABLE NVARCHAR(5),
        VIEW_DEFINITION NVARCHAR(MAX)
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllViewMetadata
        SELECT
            V.TABLE_CATALOG,
            V.TABLE_SCHEMA,
            V.TABLE_NAME,
            V.CHECK_OPTION,
            V.IS_UPDATABLE,
            OBJECT_DEFINITION(OBJECT_ID(V.TABLE_CATALOG + ''.'' + V.TABLE_SCHEMA + ''.'' + V.TABLE_NAME))
        FROM [?].INFORMATION_SCHEMA.VIEWS V;
    END';

    SELECT * FROM ##AllViewMetadata;
END;
@
