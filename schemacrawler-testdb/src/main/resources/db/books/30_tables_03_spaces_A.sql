-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Table and column with mixed-case name 
-- (SchemaCrawler does not quote mixed case names by default,
-- but SQL queries like counts may fail on mixed-case names if they are not quoted.)
CREATE TABLE "Celebrities"
(
  "Id" INTEGER NOT NULL,
  Name VARCHAR(20),
  CONSTRAINT "PK_Celebrities" PRIMARY KEY ("Id")
)
;

-- Table, column and primary key names with spaces 
-- Columns with reserved words as the name 
CREATE TABLE "Celebrity Updates"
(
  "Celebrity Id" INTEGER NOT NULL,
  "UPDATE" VARCHAR(20),
  CONSTRAINT "PK Celebrity Updates" PRIMARY KEY ("Celebrity Id"),
  FOREIGN KEY ("Celebrity Id") REFERENCES "Celebrities" ("Id")
)
;
