-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Types
-- Informix syntax
CREATE DISTINCT TYPE NAME_TYPE AS VARCHAR(100);
CREATE DISTINCT TYPE AGE_TYPE AS INTEGER;

-- Table using types
CREATE TABLE Customers
(
  Id INTEGER NOT NULL,
  FirstName NAME_TYPE NOT NULL,
  LastName NAME_TYPE NOT NULL,
  Age AGE_TYPE,
  PRIMARY KEY (Id) CONSTRAINT PK_Customers
)
;
