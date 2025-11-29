-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures
-- SAP HANA syntax
CREATE OR REPLACE PROCEDURE New_Publisher(NewPublisher VARCHAR2, Publisher VARCHAR2)
AS
BEGIN
  Publisher = NewPublisher;
END
