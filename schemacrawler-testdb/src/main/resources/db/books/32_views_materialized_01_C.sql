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
