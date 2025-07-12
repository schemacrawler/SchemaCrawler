-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Indexes
CREATE UNIQUE INDEX UIDX_BookAuthors ON BookAuthors(BookId, AuthorId)
;
CREATE INDEX IDX_B_Authors ON Authors(LastName, FirstName)
;
CREATE INDEX IDX_A_Authors ON Authors(City ASC, State DESC, PostalCode, Country)
;
