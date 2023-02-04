-- Table with auto-incremented column
-- DuckDB syntax
CREATE TABLE Publishers
(
  Id INT PRIMARY KEY DEFAULT NEXTVAL('Publisher_Id_Seq'),
  Publisher VARCHAR(255)
)
;
