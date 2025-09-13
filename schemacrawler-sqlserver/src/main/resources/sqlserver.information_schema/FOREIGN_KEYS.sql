SELECT
    DB_NAME() AS PKTABLE_CAT,
    SCHEMA_NAME(pk_tables.schema_id) AS PKTABLE_SCHEM,
    pk_tables.name AS PKTABLE_NAME,
    pk_columns.name AS PKCOLUMN_NAME,

    DB_NAME() AS FKTABLE_CAT,
    SCHEMA_NAME(fk_tables.schema_id) AS FKTABLE_SCHEM,
    fk_tables.name AS FKTABLE_NAME,
    fk_columns.name AS FKCOLUMN_NAME,

    fkc.constraint_column_id AS KEY_SEQ,

    CASE fk.delete_referential_action
        WHEN 0 THEN 3  -- importedKeyNoAction
        WHEN 1 THEN 0  -- importedKeyCascade
        WHEN 2 THEN 2  -- importedKeySetNull
        WHEN 3 THEN 4  -- importedKeySetDefault
    END AS DELETE_RULE,

    CASE fk.update_referential_action
        WHEN 0 THEN 3  -- importedKeyNoAction
        WHEN 1 THEN 0  -- importedKeyCascade
        WHEN 2 THEN 2  -- importedKeySetNull
        WHEN 3 THEN 4  -- importedKeySetDefault
    END AS UPDATE_RULE,

    fk.name AS FK_NAME,
    NULL AS PK_NAME,

    7 AS DEFERRABILITY -- SQL Server foreign keys are not deferrable
FROM
    sys.foreign_keys fk
    INNER JOIN sys.foreign_key_columns fkc
        ON fk.object_id = fkc.constraint_object_id
    INNER JOIN sys.tables pk_tables
        ON fkc.referenced_object_id = pk_tables.object_id
    INNER JOIN sys.columns pk_columns
        ON pk_tables.object_id = pk_columns.object_id
            AND fkc.referenced_column_id = pk_columns.column_id
    INNER JOIN sys.tables fk_tables
        ON fkc.parent_object_id = fk_tables.object_id
    INNER JOIN sys.columns fk_columns
        ON fk_tables.object_id = fk_columns.object_id
            AND fkc.parent_column_id = fk_columns.column_id
WHERE
    SCHEMA_NAME(pk_tables.schema_id) = '${schema-name}'
