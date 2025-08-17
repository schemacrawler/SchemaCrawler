-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT name, value, description
FROM (
  -- Product Name
  SELECT 'PRODUCT_NAME' AS name,
         'SQLite' AS value,
         'Product family name (embedded database engine)' AS description

  UNION ALL

  -- Version
  SELECT 'PRODUCT_VERSION' AS name,
         sqlite_version() AS value,
         'SQLite engine version' AS description

  UNION ALL

  -- Database File Name
  SELECT 'DATABASE_FILE' AS name,
         file AS value,
         'Logical name and file path of attached database' AS description
  FROM pragma_database_list
  WHERE seq = 0
  
  UNION ALL

  -- Encoding
  SELECT 'ENCODING' AS name,
         encoding AS value,
         'Text encoding used in the database (e.g., UTF-8)' AS description
  FROM pragma_encoding
  
  UNION ALL

  -- Page Size
  SELECT 'PAGE_SIZE' AS name,
         page_size AS value,
         'Size of each database page in bytes' AS description
  FROM pragma_page_size

  UNION ALL

  -- Journal Mode
  SELECT 'JOURNAL_MODE' AS name,
         journal_mode AS value,
         'Transaction journaling mode (e.g., DELETE, WAL)' AS description
  FROM pragma_journal_mode
  
  UNION ALL

  -- Foreign Key Enforcement
  SELECT 'FOREIGN_KEYS' AS name,
         CASE foreign_keys WHEN 1 THEN 'true' ELSE 'false' END AS value,
         'Whether foreign key constraints are enforced' AS description  
  FROM pragma_foreign_keys        
)
