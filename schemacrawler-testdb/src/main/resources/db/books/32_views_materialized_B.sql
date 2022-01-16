-- Materialized views
-- SQL Server syntax for indexed view
CREATE VIEW AuthorsCountries WITH SCHEMABINDING AS SELECT Id, FirstName, LastName, Country FROM dbo.Authors
;

CREATE UNIQUE CLUSTERED INDEX IDX_Clustered_AuthorsCountries ON AuthorsCountries (FirstName, LastName)
;
