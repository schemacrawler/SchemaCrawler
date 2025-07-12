-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  USERS.USERNAME,
  USERS.USER_ID,
  USERS.ORACLE_MAINTAINED AS SYSTEM,
  USERS.CREATED
FROM
  ${catalogscope}_USERS USERS
ORDER BY
  USERS.USERNAME
