-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

-- Remarks on primary and foreign keys, indexes and other constraints (unique constraint)
-- Microsoft SQL Server syntax

EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   N'Primary key constraint on βιβλία',
   'SCHEMA', 'dbo', 'TABLE', N'Βιβλία', 'CONSTRAINT', N'PK_βιβλία'
;

EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Foreign key marking relationship of authors to books',
   'SCHEMA', 'dbo', 'TABLE', 'BookAuthors', 'CONSTRAINT', 'Z_FK_Author'
;

EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Unique constraint on previous edition of books',
   'SCHEMA', 'dbo', 'TABLE', 'Books', 'CONSTRAINT', 'U_PreviousEdition'
;

EXECUTE SP_ADDEXTENDEDPROPERTY 'MS_Description', 
   'Index on author''s location',
   'SCHEMA', 'dbo', 'TABLE', 'Authors', 'INDEX', 'IDX_A_Authors'
;
