-- Table with a CLOB type (which may be called TEXT in some databases)
-- and with unusual SQL types
CREATE TABLE Coupons
(
  Id INTEGER NOT NULL,
  Data CLOB,
  Coupons INT ARRAY DEFAULT ARRAY[], 
  Books VARCHAR(20) ARRAY[10],
  CONSTRAINT PK_Coupons PRIMARY KEY (Id)
)
;
