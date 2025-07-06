IF OBJECT_ID('tempdb..#schcrwlr_CollectSynonymMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectSynonymMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectSynonymMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllSynonymMetadata') IS NOT NULL
        DROP TABLE ##AllSynonymMetadata;

    CREATE TABLE ##AllSynonymMetadata (
        SYNONYM_CATALOG SYSNAME,
        SYNONYM_SCHEMA SYSNAME,
        SYNONYM_NAME SYSNAME,
        REFERENCED_OBJECT_SERVER SYSNAME,
        REFERENCED_OBJECT_CATALOG SYSNAME,
        REFERENCED_OBJECT_SCHEMA SYSNAME,
        REFERENCED_OBJECT_NAME SYSNAME
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllSynonymMetadata
        SELECT
            DB_NAME() AS SYNONYM_CATALOG,
            SCHEMA_NAME(SCHEMA_ID) AS SYNONYM_SCHEMA,
            NAME AS SYNONYM_NAME,
            PARSENAME(BASE_OBJECT_NAME, 4),
            PARSENAME(BASE_OBJECT_NAME, 3),
            PARSENAME(BASE_OBJECT_NAME, 2),
            PARSENAME(BASE_OBJECT_NAME, 1)
        FROM 
            [?].SYS.SYNONYMS;
    END';

    SELECT * FROM ##AllSynonymMetadata;
END;
@