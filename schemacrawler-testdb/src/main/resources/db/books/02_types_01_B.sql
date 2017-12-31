-- Table with a CLOB type (which may be called TEXT in some databases)
-- and with unusual SQL types
CREATE TABLE Coupons
(
  Id INTEGER NOT NULL,
  Data CLOB,
  Coupons INTEGER, 
  Books VARCHAR(20),
  CONSTRAINT PK_Coupons PRIMARY KEY (Id)
)
;
