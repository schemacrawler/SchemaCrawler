-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Hidden columns
-- IBM DB2 syntax
CREATE TABLE X_CUSTOMERS
(
  CUSTOMERID INTEGER NOT NULL,
  CUSTOMER_NAME VARCHAR(80),
  SOCIAL_SECURITY_NUMBER CHAR(9) IMPLICITLY HIDDEN
);
