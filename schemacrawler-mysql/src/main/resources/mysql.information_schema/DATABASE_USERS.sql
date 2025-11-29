-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  USER AS USERNAME,
  HOST,
  PLUGIN,
  AUTHENTICATION_STRING,
  PASSWORD_LAST_CHANGED,
  PASSWORD_EXPIRED,
  ACCOUNT_LOCKED
FROM
  MYSQL.USER
ORDER
  BY USER
