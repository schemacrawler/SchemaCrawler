-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Remarks on primary and foreign keys, indexes and other constraints (unique constraint)

COMMENT ON CONSTRAINT PK_βιβλία IS 'Primary key constraint on βιβλία'
;

COMMENT ON CONSTRAINT Z_FK_Author IS 'Foreign key marking relationship of authors to books'
;

COMMENT ON CONSTRAINT U_PreviousEdition IS 'Unique constraint on previous edition of books'
;

COMMENT ON INDEX IDX_A_Authors IS 'Index on author''s location'
;
