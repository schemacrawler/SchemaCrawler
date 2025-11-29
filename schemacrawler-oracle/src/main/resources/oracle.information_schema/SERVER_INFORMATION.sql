-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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

  -- Collation (NLS_SORT or NLS_COMP)
  SELECT 'COLLATION' AS NAME,
         VALUE AS VALUE,
         'NLS collation setting (NLS_SORT)' AS DESCRIPTION
  FROM NLS_DATABASE_PARAMETERS
  WHERE PARAMETER = 'NLS_SORT'

  UNION ALL

  -- Character Set: NLS_CHARACTERSET
  SELECT 'NLS_CHARACTERSET' AS NAME,
         VALUE AS VALUE,
         'Database character set encoding for CHAR/VARCHAR2/CLOB' AS DESCRIPTION
  FROM NLS_DATABASE_PARAMETERS
  WHERE PARAMETER = 'NLS_CHARACTERSET'

  UNION ALL

  -- Character Set: NLS_NCHAR_CHARACTERSET
  SELECT 'NLS_NCHAR_CHARACTERSET' AS NAME,
         VALUE AS VALUE,
         'Character set for NCHAR/NVARCHAR2/NCLOB types' AS DESCRIPTION
  FROM NLS_DATABASE_PARAMETERS
  WHERE PARAMETER = 'NLS_NCHAR_CHARACTERSET'

  UNION ALL

  -- Locale: NLS_LANGUAGE
  SELECT 'NLS_LANGUAGE' AS NAME,
         VALUE AS VALUE,
         'Language used for sorting, day/month names, and formatting' AS DESCRIPTION
  FROM NLS_DATABASE_PARAMETERS
  WHERE PARAMETER = 'NLS_LANGUAGE'

  UNION ALL

  -- Locale: NLS_TERRITORY
  SELECT 'NLS_TERRITORY' AS NAME,
         VALUE AS VALUE,
         'Territory setting for date, currency, and numeric formats' AS DESCRIPTION
  FROM NLS_DATABASE_PARAMETERS
  WHERE PARAMETER = 'NLS_TERRITORY'

  UNION ALL

  -- Product Name
  SELECT 'PRODUCT_NAME' AS NAME,
         PRODUCT AS VALUE,
         'Oracle Database product name and edition' AS DESCRIPTION
  FROM SYS.PRODUCT_COMPONENT_VERSION
  WHERE UPPER(PRODUCT) LIKE 'ORACLE%'

  UNION ALL

  -- Product Version
  SELECT 'PRODUCT_VERSION' AS NAME,
         VERSION AS VALUE,
         'Oracle Database software version' AS DESCRIPTION
  FROM SYS.PRODUCT_COMPONENT_VERSION
  WHERE UPPER(PRODUCT) LIKE 'ORACLE%'
)
