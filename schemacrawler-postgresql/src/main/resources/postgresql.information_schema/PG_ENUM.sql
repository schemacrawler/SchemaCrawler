SELECT
  CURRENT_DATABASE()::INFORMATION_SCHEMA.SQL_IDENTIFIER AS TYPE_CATALOG,
  n.nspname AS TYPE_SCHEMA,
  t.typname AS TYPE_NAME,
  e.enumlabel AS ENUM_LABEL
FROM
  pg_enum e
  INNER JOIN pg_type t
    ON e.enumtypid = t.oid
  INNER JOIN pg_catalog.pg_namespace n
    ON n.oid = t.typnamespace
WHERE
  t.typname = '${column-data-type}'
