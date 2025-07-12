-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  'database' AS NAME,
  database() AS VALUE,
  'MySQL database name' AS DESCRIPTION
UNION
SELECT
  'server_uuid' AS NAME,
  @@server_uuid AS VALUE,
  'Server UUID' AS DESCRIPTION
UNION
SELECT
  'hostname' AS NAME,
  @@hostname AS VALUE,
  'Host name' AS DESCRIPTION
