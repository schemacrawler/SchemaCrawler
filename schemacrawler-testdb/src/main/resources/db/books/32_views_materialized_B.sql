-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Materialized views
-- SQL Server syntax for indexed view
CREATE VIEW AuthorsCountries WITH SCHEMABINDING AS SELECT Id, FirstName, LastName, Country FROM dbo.Authors
;

CREATE UNIQUE CLUSTERED INDEX IDX_Clustered_AuthorsCountries ON AuthorsCountries (FirstName, LastName)
;
