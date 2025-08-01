-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  CURRENT_DATABASE()::INFORMATION_SCHEMA.SQL_IDENTIFIER AS SEQUENCE_CATALOG,
  SEQUENCE_SCHEMA,
  SEQUENCE_NAME,
  INCREMENT,
  NULL AS START_VALUE,
  MINIMUM_VALUE,
  MAXIMUM_VALUE,
  CYCLE_OPTION
FROM
  INFORMATION_SCHEMA.SEQUENCES
ORDER BY
  SEQUENCE_CATALOG,
  SEQUENCE_SCHEMA,
  SEQUENCE_NAME
