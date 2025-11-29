-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Types
CREATE TYPE NAME_TYPE AS VARCHAR(100);
CREATE TYPE AGE_TYPE AS SMALLINT;

-- Table using types
CREATE TABLE Customers
(
  Id INTEGER NOT NULL,
  FirstName NAME_TYPE NOT NULL,
  LastName NAME_TYPE NOT NULL,
  Age AGE_TYPE,
  CONSTRAINT PK_Customers PRIMARY KEY (Id)
)
;
