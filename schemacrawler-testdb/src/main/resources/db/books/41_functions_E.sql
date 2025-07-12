-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Functions
-- SAP HANA syntax
CREATE FUNCTION CustomAdd(One INTEGER, Two INTEGER)
  RETURNS CustomAdd INTEGER
AS
BEGIN
  CustomAdd = One + Two;
END
