-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Generated columns
-- Microsoft SQL Server syntax
CREATE TABLE X_EMPLOYEES
( 
  EMPLOYEEID INTEGER NOT NULL,
  EMPLOYEE_NAME VARCHAR(30), 
  START_DATE DATE, 
  END_DATE DATE, 
  ANNUAL_SALARY INTEGER,
  HOURLY_RATE AS (ANNUAL_SALARY/2080),
  ACTIVE AS (CASE WHEN END_DATE IS NULL THEN 'Y' ELSE 'N' END)
); 
