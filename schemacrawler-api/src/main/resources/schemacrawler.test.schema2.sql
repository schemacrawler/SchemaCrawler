-- Tables
CREATE TABLE Regions
(
  City VARCHAR(50) NOT NULL,
  State VARCHAR(2) NOT NULL,
  PostalCode VARCHAR(10) NOT NULL,
  Country VARCHAR(50) NOT NULL,
  CONSTRAINT PK_Customers PRIMARY KEY (PostalCode, Country)
);
CREATE TABLE Sales
(
  PostalCode VARCHAR(10) NOT NULL,
  Country VARCHAR(50) NOT NULL,
  BookId INTEGER,
  PeriodEndDate DATE,
  TotalAmount FLOAT,  
  CONSTRAINT FK_Sales_Regions FOREIGN KEY (PostalCode, Country) REFERENCES Regions (PostalCode, Country)
);

-- Data

INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Duluth','ND','82539','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Ogdensburg','AZ','50786','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Del Rio','CA','24179','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Baltimore','AK','34702','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Woburn','NV','27392','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Kent','OH','91624','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Clairton','MA','42875','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Fayetteville','MS','41233','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Watertown','SC','20239','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Lake Forest','AK','26131','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Hopkinsville','IN','54618','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('San Juan','MD','30552','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Nashua','SC','27829','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Martinsburg','KY','76672','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Arvada','KY','17131','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Effingham','TX','44207','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Warren','VA','42210','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Tacoma','PA','70472','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('El Segundo','VA','13078','USA');
INSERT INTO Regions (City,State,PostalCode,Country) VALUES ('Marlborough','WV','88272','USA');

INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('13078','USA',1,{d '2001-09-29'},14895);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('17131','USA',2,{d '2002-04-30'},17887);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('20239','USA',3,{d '2004-08-28'},18747);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('24179','USA',4,{d '2009-02-14'},5885);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('26131','USA',5,{d '2004-11-03'},15348);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('13078','USA',6,{d '2006-06-11'},7840);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('17131','USA',7,{d '2003-03-21'},18431);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('20239','USA',8,{d '2008-08-15'},17514);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('24179','USA',9,{d '2004-04-10'},14069);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('26131','USA',10,{d '2005-12-03'},17069);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('13078','USA',1,{d '2001-07-18'},18257);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('17131','USA',2,{d '2004-11-13'},16317);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('20239','USA',3,{d '2005-07-27'},6712);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('24179','USA',4,{d '2005-12-24'},11103);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('26131','USA',5,{d '2001-12-02'},14798);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('13078','USA',6,{d '2003-12-28'},6083);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('17131','USA',7,{d '2000-03-08'},6951);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('20239','USA',8,{d '2008-03-29'},11013);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('24179','USA',9,{d '2000-05-16'},13595);
INSERT INTO Sales (PostalCode,Country,BookId,PeriodEndDate,TotalAmount) VALUES ('26131','USA',10,{d '2000-09-20'},14476);
