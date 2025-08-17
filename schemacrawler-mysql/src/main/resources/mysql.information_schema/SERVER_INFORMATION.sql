-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT name, value, description
FROM (
  -- Version
  SELECT 'PRODUCT_VERSION' AS name,
         VERSION() AS value,
         'Full version string (e.g., 8.0.34)' AS description

  UNION ALL

  -- Default Storage Engine
  SELECT 'DEFAULT_ENGINE' AS name,
         VARIABLE_VALUE AS value,
         'Default storage engine for new tables' AS description
  FROM performance_schema.global_variables
  WHERE VARIABLE_NAME = 'default_storage_engine'

  UNION ALL

  -- Database Name
  SELECT 'DATABASE_NAME' AS name,
         DATABASE() AS value,
         'Current default database name' AS description

  UNION ALL

  -- Server Collation
  SELECT 'SERVER_COLLATION' AS name,
         @@collation_server AS value,
         'Default collation for server-level operations' AS description

  UNION ALL

  -- Server Character Set
  SELECT 'SERVER_CHARACTER_SET' AS name,
         @@character_set_server AS value,
         'Default character set for server-level operations' AS description

  UNION ALL

  -- Database Character Set
  SELECT 'DATABASE_CHARACTER_SET' AS name,
         @@character_set_database AS value,
         'Character set of the current database' AS description

  UNION ALL

  -- Time Zone
  SELECT 'TIME_ZONE' AS name,
         @@time_zone AS value,
         'Server time zone setting' AS description

  UNION ALL

  -- SQL Mode
  SELECT 'SQL_MODE' AS name,
         @@sql_mode AS value,
         'SQL mode flags affecting syntax and behavior' AS description

  UNION ALL

  -- Version Comment
  SELECT 'PRODUCT_NAME' AS name,
         @@version_comment AS value,
         'Additional build info (e.g., MySQL Community Server)' AS description
) AS server_information
