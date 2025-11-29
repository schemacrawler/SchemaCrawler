-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Global temporary table
-- Apache Derby Syntax
DECLARE GLOBAL TEMPORARY TABLE TEMP_AUTHOR_LIST
(
  Id INTEGER NOT NULL,
  FirstName VARCHAR(20) NOT NULL,
  LastName VARCHAR(20) NOT NULL
)
NOT LOGGED
;
