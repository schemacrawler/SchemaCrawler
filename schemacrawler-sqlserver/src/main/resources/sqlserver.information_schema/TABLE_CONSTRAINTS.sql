-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
    DB_NAME() AS CONSTRAINT_CATALOG,
    CONSTRAINT_SCHEMA,
    CONSTRAINT_NAME,
    TABLE_CATALOG,
    TABLE_SCHEMA,
    TABLE_NAME,
    CONSTRAINT_TYPE,
    IS_DEFERRABLE,
    INITIALLY_DEFERRED
FROM
   INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE
   CONSTRAINT_SCHEMA = '${schema-name}'
