-- Table with auto-incremented column, generated
-- with an unnamed primary key
-- IBM DB2, Apache Derby and H2 syntax
CREATE TABLE Publishers
(
  Id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  Publisher VARCHAR(255)
)
;
