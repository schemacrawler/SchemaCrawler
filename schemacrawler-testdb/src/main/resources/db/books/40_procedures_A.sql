-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures
CREATE PROCEDURE New_Publisher(OUT Publisher VARCHAR(50))
SPECIFIC New_Publisher_Force_Value
  SET Publisher = 'New Publisher'
;

CREATE PROCEDURE New_Publisher(IN NewPublisher VARCHAR(50), OUT Publisher VARCHAR(50))
  SET Publisher = NewPublisher
;
