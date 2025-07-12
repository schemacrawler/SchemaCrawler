IF OBJECT_ID('tempdb..#schcrwlr_CollectSequenceMetadata') IS NOT NULL
    DROP PROCEDURE #schcrwlr_CollectSequenceMetadata;
@

CREATE PROCEDURE #schcrwlr_CollectSequenceMetadata
AS
BEGIN
    SET NOCOUNT ON;

    IF OBJECT_ID('tempdb..##AllSequenceMetadata') IS NOT NULL
        DROP TABLE ##AllSequenceMetadata;

    CREATE TABLE ##AllSequenceMetadata (
        SEQUENCE_CATALOG SYSNAME,
        SEQUENCE_SCHEMA SYSNAME,
        SEQUENCE_NAME SYSNAME,
        DATA_TYPE NVARCHAR(128),
        NUMERIC_PRECISION INT,
        NUMERIC_PRECISION_RADIX INT,
        NUMERIC_SCALE INT,
        START_VALUE BIGINT,
        MINIMUM_VALUE BIGINT,
        MAXIMUM_VALUE BIGINT,
        INCREMENT BIGINT,
        CYCLE_OPTION NVARCHAR(3),
        DECLARED_DATA_TYPE NVARCHAR(128),
        DECLARED_NUMERIC_PRECISION INT,
        DECLARED_NUMERIC_SCALE INT
    );

    EXEC sp_msforeachdb N'
    IF ''?'' NOT IN (''master'',''model'',''msdb'',''tempdb'')
    BEGIN
        INSERT INTO ##AllSequenceMetadata
        SELECT
            SEQUENCE_CATALOG,
            SEQUENCE_SCHEMA,
            SEQUENCE_NAME,
            DATA_TYPE,
            NUMERIC_PRECISION,
            NUMERIC_PRECISION_RADIX,
            NUMERIC_SCALE,
            CAST(START_VALUE AS BIGINT),
            CAST(MINIMUM_VALUE AS BIGINT),
            CAST(MAXIMUM_VALUE AS BIGINT),
            CAST(INCREMENT AS BIGINT),
            CYCLE_OPTION,
            DECLARED_DATA_TYPE,
            DECLARED_NUMERIC_PRECISION,
            DECLARED_NUMERIC_SCALE
        FROM [?].INFORMATION_SCHEMA.SEQUENCES
        ORDER BY
            SEQUENCE_CATALOG,
            SEQUENCE_SCHEMA,
            SEQUENCE_NAME;
    END';

    SELECT * FROM ##AllSequenceMetadata;
END;
@