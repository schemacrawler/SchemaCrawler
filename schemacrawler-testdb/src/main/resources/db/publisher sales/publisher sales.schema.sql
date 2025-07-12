-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0

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
  Coupon_Id INTEGER,
  PeriodEndDate DATE,
  TotalAmount FLOAT,  
  SalesDataId INTEGER,
  CONSTRAINT FK_Sales_Regions FOREIGN KEY (PostalCode, Country) REFERENCES Regions (PostalCode, Country),
  CONSTRAINT FK_Sales_SalesData FOREIGN KEY (SalesDataId) REFERENCES SalesData (SalesDataId)
);

-- AcrossDatabase test for foreign keys
ALTER TABLE Sales ADD CONSTRAINT FK_Sales_Book FOREIGN KEY (BookId) REFERENCES BOOKS.Books (Id);
