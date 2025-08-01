-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  NULL AS TABLE_CATALOG,
  COLUMNS.OWNER AS TABLE_SCHEMA,
  COLUMNS.TABLE_NAME,
  COLUMNS.COLUMN_NAME
FROM
  ${catalogscope}_TAB_COLS COLUMNS
  INNER JOIN ${catalogscope}_USERS USERS
    ON COLUMNS.OWNER = USERS.USERNAME
      AND USERS.ORACLE_MAINTAINED = 'N'
      AND NOT REGEXP_LIKE(USERS.USERNAME, '^APEX_[0-9]{6}$')
      AND NOT REGEXP_LIKE(USERS.USERNAME, '^FLOWS_[0-9]{5}$')
      AND NOT REGEXP_LIKE(USERS.USERNAME, '^OPS\$ORACLE$')
WHERE
  REGEXP_LIKE(COLUMNS.OWNER, '${schema-inclusion-rule}')
  AND REGEXP_LIKE(COLUMNS.OWNER || '.' || COLUMNS.TABLE_NAME, '${table-inclusion-rule}')
  AND COLUMNS.TABLE_NAME NOT LIKE 'BIN$%'
  AND NOT REGEXP_LIKE(COLUMNS.TABLE_NAME, '^(SYS_IOT|MDOS|MDRS|MDRT|MDOT|MDXT)_.*$')
  AND COLUMNS.HIDDEN_COLUMN = 'YES'
ORDER BY
  COLUMNS.OWNER,
  COLUMNS.TABLE_NAME,
  COLUMNS.COLUMN_NAME
