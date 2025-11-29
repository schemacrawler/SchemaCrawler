-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Remarks
COMMENT ON TABLE EXTRA_PK IS 'Extra table with just a primary key'
;
COMMENT ON COLUMN PUBLICATIONS.TITLE IS 'Publication title'
;

-- Domains
CREATE DOMAIN VALID_STRING AS VARCHAR(20) DEFAULT 'NO VALUE' CHECK (VALUE IS NOT NULL AND CHARACTER_LENGTH(VALUE) > 2);

-- Temporary table
CREATE GLOBAL TEMPORARY TABLE TEMP1
(
  ID INT PRIMARY KEY, 
  SCORES INT ARRAY DEFAULT ARRAY[], 
  NAMES VARCHAR(20) ARRAY[10], 
  DATA VALID_STRING
)
ON COMMIT DELETE ROWS;
