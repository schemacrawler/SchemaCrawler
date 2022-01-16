-- Table with a CLOB type (which may be called TEXT in some databases)
-- and with unusual SQL types
-- Informix syntax
CREATE TABLE Coupons
(
  Id INTEGER NOT NULL,
  Data TEXT,
  Coupons INTEGER, 
  Books VARCHAR(20),
  PRIMARY KEY (Id) CONSTRAINT PK_Coupons
)
;
