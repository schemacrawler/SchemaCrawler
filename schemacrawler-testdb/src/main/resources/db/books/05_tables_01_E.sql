-- Table with identity column, generated
-- Microsoft SQL Server syntax
CREATE TABLE Publishers
(
  Id INTEGER NOT NULL IDENTITY,
  Publisher VARCHAR(255),
  CONSTRAINT PK_Publishers PRIMARY KEY (Id)
)
;
