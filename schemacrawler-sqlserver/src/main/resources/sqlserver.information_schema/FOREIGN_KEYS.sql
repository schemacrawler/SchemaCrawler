SELECT
    DB_NAME() AS PKTABLE_QUALIFIER,
    SCHEMA_NAME(pk.schema_id) AS PKTABLE_OWNER,
    pk.name AS PKTABLE_NAME,
    pkc.name AS PKCOLUMN_NAME,

    DB_NAME() AS FKTABLE_QUALIFIER,
    SCHEMA_NAME(fk.schema_id) AS FKTABLE_OWNER,
    fk.name AS FKTABLE_NAME,
    fkc.name AS FKCOLUMN_NAME,

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
    INNER JOIN sys.tables pk
        ON fkc.referenced_object_id = pk.object_id
    INNER JOIN sys.columns pkc
        ON pk.object_id = pkc.object_id
            AND fkc.referenced_column_id = pkc.column_id
    INNER JOIN sys.tables fk_table
        ON fkc.parent_object_id = fk_table.object_id
    INNER JOIN sys.columns fkc
        ON fk_table.object_id = fkc.object_id
            AND fkc.parent_column_id = fkc.column_id
WHERE
    SCHEMA_NAME(pk.schema_id) = '${schema-name}'
