-- Materialized views
-- Oracle and PostgreSQL syntax
CREATE MATERIALIZED VIEW AuthorsCountries AS SELECT Id, FirstName, LastName, Country FROM Authors
;
