-- Tables
CREATE TABLE Regions
(
  City VARCHAR(50) NOT NULL,
  State VARCHAR(2) NOT NULL,
  PostalCode VARCHAR(10) NOT NULL,
  Country VARCHAR(50) NOT NULL,
  CONSTRAINT PK_Customers PRIMARY KEY (PostalCode, Country)
);

CREATE TABLE SalesData
(
  SalesDataId INTEGER,
  YearlyAmount FLOAT,  
  CONSTRAINT UQ_Customers UNIQUE (SalesDataId)
);

CREATE TABLE Sales
(
  PostalCode VARCHAR(10) NOT NULL,
  Country VARCHAR(50) NOT NULL,
  BookId INTEGER,
  PeriodEndDate DATE,
  TotalAmount FLOAT,  
  SalesDataId INTEGER,
  CONSTRAINT FK_Sales_Regions FOREIGN KEY (PostalCode, Country) REFERENCES Regions (PostalCode, Country),
  CONSTRAINT FK_Sales_SalesData FOREIGN KEY (SalesDataId) REFERENCES SalesData (SalesDataId)
);
