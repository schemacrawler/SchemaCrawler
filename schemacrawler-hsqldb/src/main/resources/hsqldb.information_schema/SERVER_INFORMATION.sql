-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT name, value, description
FROM (
  VALUES
    ('PRODUCT_NAME',
     'HyperSQL (HSQLDB)',
     'Product family name (Java-based embedded/remote DB)'),

    ('PRODUCT_VERSION',
     DATABASE_VERSION(),
     'HSQLDB engine version'),
     
    ('CATALOG_NAME', 
     (SELECT CATALOG_NAME FROM INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME), 
     'Current catalog name')
     
) AS server_information(name, value, description)
