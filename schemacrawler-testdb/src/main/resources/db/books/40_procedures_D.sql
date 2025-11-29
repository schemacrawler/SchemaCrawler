-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures
-- Oracle syntax
CREATE OR REPLACE PROCEDURE New_Publisher(NewPublisher IN VARCHAR2, Publisher OUT VARCHAR2)
IS
BEGIN
  Publisher := NewPublisher;
END;
