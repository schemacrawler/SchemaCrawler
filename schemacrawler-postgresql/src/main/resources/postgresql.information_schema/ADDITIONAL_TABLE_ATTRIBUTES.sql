SELECT
  current_database()::information_schema.sql_identifier AS TABLE_CATALOG,
  nc.nspname::information_schema.sql_identifier AS TABLE_SCHEMA,
  c.relname::information_schema.sql_identifier AS TABLE_NAME,
  c.*
FROM
  pg_catalog.pg_class c
  INNER JOIN pg_catalog.pg_namespace nc
    ON c.relowner = nc.nspowner
WHERE
  c.relkind IN ('r', 'v')
