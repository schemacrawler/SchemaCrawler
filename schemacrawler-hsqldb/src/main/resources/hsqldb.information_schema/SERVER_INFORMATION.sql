-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT 'DATABASE' AS NAME,
       'Current database name' AS DESCRIPTION,
       DATABASE() AS VALUE
UNION ALL
SELECT 'VERSION',
       'HyperSQL database engine version',
       VERSION()
UNION ALL
SELECT 'SCHEMA_DEFAULT_COLLATION',
       'Default collation for current schema',
       (SELECT DEFAULT_COLLATION_NAME
        FROM INFORMATION_SCHEMA.SCHEMATA
        WHERE SCHEMA_NAME = CURRENT_SCHEMA)
UNION ALL
SELECT 'AVAILABLE_COLLATIONS',
       'Comma-separated list of available collations',
       (SELECT GROUP_CONCAT(COLLATION_NAME, ', ')
        FROM INFORMATION_SCHEMA.COLLATIONS)
UNION ALL
SELECT 'CATALOG_NAME',
       'Catalog name of the current database',
       CATALOG_NAME
FROM INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME;
