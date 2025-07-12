-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

SELECT
  'ServerName' AS NAME,
  SERVERPROPERTY('ServerName') AS VALUE,
  'Both the Windows server and instance information associated with a specified instance of SQL Server.' AS DESCRIPTION
UNION ALL
SELECT
  'InstanceName' AS NAME,
  SERVERPROPERTY('InstanceName') AS VALUE,
  'Name of the instance to which the user is connected. Returns NULL if the instance name is the default instance.' AS DESCRIPTION
UNION ALL
SELECT
  'IsClustered' AS NAME,
  SERVERPROPERTY('IsClustered') AS VALUE,
  ' Server instance is configured in a failover cluster.' AS DESCRIPTION
