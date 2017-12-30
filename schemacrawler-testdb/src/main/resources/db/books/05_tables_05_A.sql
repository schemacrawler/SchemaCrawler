-- Global temporary table, with user-defined types
CREATE GLOBAL TEMPORARY TABLE TEMP_AUTHOR_LIST
(
  Id INTEGER NOT NULL,
  FirstName NAME_TYPE NOT NULL,
  LastName NAME_TYPE NOT NULL,
  Age AGE_TYPE,
  CONSTRAINT PK_Tmp_Authors PRIMARY KEY (Id)
)
;
