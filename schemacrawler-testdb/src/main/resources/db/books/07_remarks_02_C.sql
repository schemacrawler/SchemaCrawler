-- Remarks on indexes
-- MySQL

DROP INDEX IDX_A_Authors ON Authors;
CREATE INDEX IDX_A_Authors ON Authors(City, State, PostalCode, Country)
COMMENT 'Index on author''s location'
;
