-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table with a CLOB type (which may be called TEXT in some databases)
-- and with unusual SQL types
CREATE TABLE Coupons
(
  Id INTEGER NOT NULL,
  Data CLOB,
  Coupons INTEGER, 
  Books VARCHAR(20),
  CONSTRAINT PK_Coupons PRIMARY KEY (Id)
)
;
