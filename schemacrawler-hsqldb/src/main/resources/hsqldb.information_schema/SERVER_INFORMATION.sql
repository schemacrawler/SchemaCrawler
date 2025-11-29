-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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

    ('DATABASE_NAME', 
     DATABASE_NAME(), 
     'Database name'),

    ('CATALOG_NAME', 
     (SELECT CATALOG_NAME FROM INFORMATION_SCHEMA.INFORMATION_SCHEMA_CATALOG_NAME), 
     'Current catalog name'),
     
    ('DATABASE_ISOLATION_LEVEL', 
     DATABASE_ISOLATION_LEVEL(), 
     'Default transaction isolation level for the database - READ COMMITTED or SERIALIZABLE'),   

    ('TRANSACTION_CONTROL', 
     TRANSACTION_CONTROL(), 
     'Returns the current transaction model for the database - LOCKS, MVLOCKS or MVCC')  
      
) AS server_information(name, value, description)
