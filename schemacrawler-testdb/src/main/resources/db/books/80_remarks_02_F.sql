-- Remarks on primary and foreign keys, indexes and other constraints (unique constraint)
-- IBM DB2 syntax, with table names

COMMENT ON CONSTRAINT βιβλία.PK_βιβλία IS 'Primary key constraint on βιβλία'
;

COMMENT ON CONSTRAINT BookAuthors.Z_FK_Author IS 'Foreign key marking relationship of authors to books'
;

COMMENT ON CONSTRAINT Books.U_PreviousEdition IS 'Unique constraint on previous edition of books'
;

COMMENT ON INDEX IDX_A_Authors IS 'Index on author''s location'
;
