-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Stored procedures
-- Informix syntax
CREATE PROCEDURE New_Publisher(Publisher VARCHAR(50))
SPECIFIC New_Publisher_Force_Value;
  UPDATE Publisher
    SET Publisher = 'New Publisher';
END PROCEDURE
@

CREATE PROCEDURE New_Publisher(Publisher VARCHAR(50), New_Publisher VARCHAR(50))
  UPDATE Publisher
    SET Publisher = 'New Publisher';
END PROCEDURE
@
