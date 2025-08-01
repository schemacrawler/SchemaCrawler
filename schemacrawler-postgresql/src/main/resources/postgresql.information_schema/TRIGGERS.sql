-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  CURRENT_DATABASE()::INFORMATION_SCHEMA.SQL_IDENTIFIER AS TRIGGER_CATALOG,
  TRIGGER_SCHEMA,
  TRIGGER_NAME,
  EVENT_MANIPULATION,
  EVENT_OBJECT_CATALOG,
  EVENT_OBJECT_SCHEMA,
  EVENT_OBJECT_TABLE,
  ACTION_ORDER,
  ACTION_CONDITION,
  ACTION_STATEMENT,
  ACTION_ORIENTATION,
  ACTION_TIMING,
  CREATED
FROM
  INFORMATION_SCHEMA.TRIGGERS
