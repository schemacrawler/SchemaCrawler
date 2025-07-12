-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Remarks on primary and foreign keys, indexes and other constraints (unique constraint)
-- PostgreSQL syntax

COMMENT ON INDEX PK_βιβλία IS 'Primary key constraint on βιβλία'
;

COMMENT ON CONSTRAINT Z_FK_Author ON BookAuthors IS 'Foreign key marking relationship of authors to books'
;

COMMENT ON CONSTRAINT U_PreviousEdition ON Books IS 'Unique constraint on previous edition of books'
;

COMMENT ON INDEX IDX_A_Authors IS 'Index on author''s location'
;
