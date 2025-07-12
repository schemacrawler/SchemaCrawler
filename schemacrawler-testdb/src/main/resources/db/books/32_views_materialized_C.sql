-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Materialized views
-- IBM DB2 materialized query table (MQT)
CREATE TABLE AuthorsCountries
(
  Id,
  FirstName,
  LastName,
  Country
)
AS
(
  SELECT
    Id,
    FirstName,
    LastName,
    Country
  FROM
    Authors
)
DATA INITIALLY DEFERRED
REFRESH DEFERRED
MAINTAINED BY SYSTEM
ENABLE QUERY OPTIMIZATION
;

SET INTEGRITY FOR AUTHORSCOUNTRIES ALL IMMEDIATE UNCHECKED;
