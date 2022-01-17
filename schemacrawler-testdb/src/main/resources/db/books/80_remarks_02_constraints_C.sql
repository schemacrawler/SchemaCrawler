-- Remarks on indexes only
-- Remarks on primary and foreign keys, constraints are not supported
-- MySQL syntax

DROP INDEX IDX_A_Authors ON Authors;
CREATE INDEX IDX_A_Authors ON Authors(City, State, PostalCode, Country)
COMMENT 'Index on author''s location'
;
