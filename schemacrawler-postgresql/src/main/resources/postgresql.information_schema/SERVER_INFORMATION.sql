-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT name, value, description
FROM (
  SELECT 'PRODUCT_NAME' AS name,
         version() AS value,
         'Full PostgreSQL version string' AS description

  UNION ALL

  -- Database Name
  SELECT 'DATABASE_NAME' AS name,
         current_database() AS value,
         'Current database name' AS description

  UNION ALL

  -- Collation: LC_COLLATE
  SELECT 'LC_COLLATE' AS name,
         pg_catalog.pg_database.datcollate AS value,
         'Collation order for string comparison' AS description
  FROM pg_catalog.pg_database
  WHERE datname = current_database()

  UNION ALL

  -- Collation: LC_CTYPE
  SELECT 'LC_CTYPE' AS name,
         pg_catalog.pg_database.datctype AS value,
         'Character classification and case conversion' AS description
  FROM pg_catalog.pg_database
  WHERE datname = current_database()

  UNION ALL

  -- Character Set / Encoding
  SELECT 'ENCODING' AS name,
         pg_encoding_to_char(encoding) AS value,
         'Database encoding (e.g., UTF8)' AS description
  FROM pg_database
  WHERE datname = current_database()

  UNION ALL

  -- Locale: datestyle
  SELECT 'DATESTYLE' AS name,
         current_setting('datestyle') AS value,
         'Date format style (e.g., ISO, MDY)' AS description

  UNION ALL

  -- Locale: timezone
  SELECT 'TIMEZONE' AS name,
         current_setting('TimeZone') AS value,
         'Current timezone setting' AS description

  UNION ALL

  -- Locale: lc_messages
  SELECT 'LC_MESSAGES' AS name,
         current_setting('lc_messages') AS value,
         'Locale for system messages' AS description

  UNION ALL

  -- Locale: lc_time
  SELECT 'LC_TIME' AS name,
         current_setting('lc_time') AS value,
         'Locale for time formatting' AS description
)
  AS SERVER_INFORMATION
