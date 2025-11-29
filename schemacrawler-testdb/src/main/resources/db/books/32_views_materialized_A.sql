-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Materialized views
-- Oracle and PostgreSQL syntax
CREATE MATERIALIZED VIEW AuthorsCountries AS SELECT Id, FirstName, LastName, Country FROM Authors
;
