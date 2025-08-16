-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT NAME, VALUE, DESCRIPTION
FROM (
  -- Global Name
  SELECT 'GLOBAL_NAME' AS NAME,
         GLOBAL_NAME AS VALUE,
         'Database global name (typically db_name.db_domain)' AS DESCRIPTION
  FROM GLOBAL_NAME

  UNION ALL

  -- Product Name
  SELECT 'PRODUCT_NAME' AS NAME,
         'Oracle Database' AS VALUE,
         'Product family name' AS DESCRIPTION
  FROM DUAL

  UNION ALL

  -- Version (e.g., 19c, 21c)
  SELECT 'VERSION' AS NAME,
         VERSION AS VALUE,
         'Full Oracle version string (e.g., 19.0.0.0.0)' AS DESCRIPTION
  FROM V$INSTANCE

  UNION ALL

  -- Edition (e.g., Enterprise Edition)
  SELECT 'EDITION' AS NAME,
         BANNER AS VALUE,
         'Edition and release info from V$VERSION' AS DESCRIPTION
  FROM V$VERSION
  WHERE BANNER LIKE 'Oracle%Edition%'

  UNION ALL

  -- Collation (NLS_SORT or NLS_COMP)
  SELECT 'COLLATION' AS NAME,
         VALUE AS VALUE,
         'NLS collation setting (NLS_SORT)' AS DESCRIPTION
  FROM NLS_DATABASE_PARAMETERS
  WHERE PARAMETER = 'NLS_SORT'
)
