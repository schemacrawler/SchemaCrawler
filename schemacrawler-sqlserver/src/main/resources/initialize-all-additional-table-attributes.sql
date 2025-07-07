IF OBJECT_ID('tempdb..#schcrwlr_CollectAdditionalTableAttributes') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectAdditionalTableAttributes;
@

CREATE PROCEDURE #schcrwlr_CollectAdditionalTableAttributes
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllAdditionalTableAttributes') IS NOT NULL
        DROP TABLE ##AllAdditionalTableAttributes;

    CREATE TABLE ##AllAdditionalTableAttributes (
        TABLE_CATALOG SYSNAME,
        TABLE_SCHEMA SYSNAME,
        TABLE_NAME SYSNAME,
        REMARKS NVARCHAR(MAX)
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'', ''model'', ''msdb'', ''tempdb'')
    BEGIN
        INSERT INTO ##AllAdditionalTableAttributes
        SELECT
            DB_NAME() AS TABLE_CATALOG,
            SCHEMA_NAME(O.SCHEMA_ID) AS TABLE_SCHEMA,
            O.NAME AS TABLE_NAME,
            CONVERT(NVARCHAR(MAX), EP.VALUE) AS REMARKS
        FROM [?].SYS.ALL_OBJECTS O
        INNER JOIN [?].SYS.EXTENDED_PROPERTIES EP
            ON O.OBJECT_ID = EP.MAJOR_ID AND EP.MINOR_ID = 0
        WHERE
            O.IS_MS_SHIPPED != 1
            AND O.TYPE = ''U''
            AND EP.MINOR_ID = 0
            AND EP.NAME = ''MS_Description'';
    END';

    SELECT * FROM ##AllAdditionalTableAttributes;
END;
@