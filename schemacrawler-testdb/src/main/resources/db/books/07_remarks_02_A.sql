-- Remarks on primary and foreign keys, indexes and other constraints (unique constraint)

COMMENT ON CONSTRAINT PK_βιβλία IS 'Primary key constraint on βιβλία'
;

COMMENT ON CONSTRAINT Z_FK_Author IS 'Foreign key marking relationship of authors to books'
;

COMMENT ON CONSTRAINT U_PreviousEdition IS 'Unique constraint on previous edition of books'
;

COMMENT ON INDEX IDX_A_Authors IS 'Index on author''s location'
;
