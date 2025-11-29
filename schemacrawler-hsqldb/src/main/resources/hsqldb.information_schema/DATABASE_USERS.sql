-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  USER_NAME AS USERNAME,
  ADMIN,
  INITIAL_SCHEMA,
  AUTHENTICATION,
  PASSWORD_DIGEST
FROM
  INFORMATION_SCHEMA.SYSTEM_USERS
