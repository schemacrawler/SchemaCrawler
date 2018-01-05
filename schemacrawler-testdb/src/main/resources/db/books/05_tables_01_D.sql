-- Table with identity column, generated
-- with an unnamed primary key
-- Oracle 12c syntax
CREATE TABLE Publishers
(
  Id INTEGER GENERATED AS IDENTITY,
  Publisher VARCHAR(255),
  PRIMARY KEY (Id)
)
;
