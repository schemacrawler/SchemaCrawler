CREATE DATABASE DATABASE_A;
USE DATABASE_A;
CREATE SCHEMA SCHEMA_A_A;
CREATE SCHEMA SCHEMA_A_B;

CREATE TABLE DBO.A_DBO_TABLE1
(
  Id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
  Name NVARCHAR(40) NOT NULL
);

CREATE TABLE DBO.A_DBO_TABLE2
(
  Id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
  Name NVARCHAR(40) NOT NULL,
  FK_COL_TO_DBO INT NOT NULL,
  CONSTRAINT FK_DBO_TO_DBO FOREIGN KEY (FK_COL_TO_DBO) REFERENCES A_DBO_TABLE1 (Id)
);

CREATE TABLE SCHEMA_A_A.A_A_TABLE1
(
  Id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
  Name NVARCHAR(40) NOT NULL
);

CREATE TABLE SCHEMA_A_A.A_A_TABLE2
(
  Id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
  Name NVARCHAR(40) NOT NULL,
  FK_COL_TO_DBO INT NOT NULL,
  FK_COL_TO_A_A INT NOT NULL,
  CONSTRAINT FK_A_A_TO_DBO FOREIGN KEY (FK_COL_TO_DBO) REFERENCES DBO.A_DBO_TABLE1 (Id),
  CONSTRAINT FK_A_A_TO_A_A FOREIGN KEY (FK_COL_TO_A_A) REFERENCES SCHEMA_A_A.A_A_TABLE1 (Id)
);

CREATE TABLE SCHEMA_A_B.A_B_TABLE2
(
  Id INT PRIMARY KEY IDENTITY(1,1) NOT NULL,
  Name NVARCHAR(40) NOT NULL,
  FK_COL_TO_DBO INT NOT NULL,
  FK_COL_TO_A_A INT NOT NULL,
  CONSTRAINT FK_A_B_TO_DBO FOREIGN KEY (FK_COL_TO_DBO) REFERENCES DBO.A_DBO_TABLE1 (Id),
  CONSTRAINT FK_A_B_TO_A_A FOREIGN KEY (FK_COL_TO_A_A) REFERENCES SCHEMA_A_A.A_A_TABLE1 (Id)
);
