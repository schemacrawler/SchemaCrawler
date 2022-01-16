-- Local temporary table
-- Informix syntax
CREATE TEMP TABLE TEMP_AUTHOR_LIST
(
  Id INTEGER NOT NULL,
  FirstName VARCHAR(20) NOT NULL,
  LastName VARCHAR(20) NOT NULL,
  PRIMARY KEY (Id)
)
;
