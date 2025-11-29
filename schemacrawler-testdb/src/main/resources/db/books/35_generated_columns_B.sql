-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Generated columns
-- IBM DB2 syntax
CREATE TABLE X_EMPLOYEES( 
  EMPLOYEEID INTEGER NOT NULL,
  EMPLOYEE_NAME VARCHAR(30), 
  START_DATE DATE, 
  END_DATE DATE,
  ANNUAL_SALARY INTEGER,
  HOURLY_RATE GENERATED ALWAYS AS (ANNUAL_SALARY/2080)
); 
