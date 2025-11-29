-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Functions
-- Informix syntax
CREATE FUNCTION CustomAdd(One INT, Two INT)
  RETURNING INT;
  RETURN One + Two;
END FUNCTION  
@

CREATE FUNCTION CustomAdd(One INT)
  RETURNING INT;
  RETURN CustomAdd(One, 1);
END FUNCTION  
@
