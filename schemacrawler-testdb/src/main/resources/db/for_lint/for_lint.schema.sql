-- SchemaCrawler
-- http://www.schemacrawler.com
-- Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
-- All rights reserved.
-- SPDX-License-Identifier: EPL-2.0


CREATE TABLE Writers
(
  Id BIGINT NOT NULL,
  FirstName VARCHAR(20) NOT NULL,
  LastName VARCHAR(20) NOT NULL,
  Address1 VARCHAR(255),
  Address2 VARCHAR(255) DEFAULT NULL NOT NULL,
  City VARCHAR(50),
  State VARCHAR(2),
  PostalCode VARCHAR(10),
  Country VARCHAR(50) DEFAULT NULL,
  Phone1 VARCHAR(10),
  Phone2 VARCHAR(15),
  Email1 VARCHAR(10),
  Email2 INT,
  Fax VARCHAR(10),
  Fax3 INT,
  HomeEmail11 VARCHAR(10),
  HomeEmail12 VARCHAR(10),
  Publication_Id BIGINT NOT NULL,
  CONSTRAINT PK_Writers PRIMARY KEY (Id),
  CONSTRAINT CHECK_UPPERCASE_State CHECK (State=UPPER(State))
)
;

CREATE TABLE Publications
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  Description VARCHAR(255),
  WriterId BIGINT NOT NULL,
  PublicationDate DATE,
  Price FLOAT,
  "UPDATE" CLOB,
  PRESS_RELEASE CLOB,
  CONSTRAINT PK_Publications PRIMARY KEY (Id),
  CONSTRAINT FK_Publications_Writer FOREIGN KEY (WriterId) REFERENCES Writers (Id)
)
;

CREATE TABLE PublicationWriters
(
  PublicationId INTEGER NOT NULL,
  WriterId BIGINT NOT NULL,
  CONSTRAINT FK_Writer FOREIGN KEY (WriterId) REFERENCES Writers (Id),
  CONSTRAINT FK_Publication FOREIGN KEY (PublicationId) REFERENCES Publications (Id)
)
;

CREATE TABLE "Global Counts"
(
  "Global Count" INTEGER
)
;

CREATE TABLE EXTRA_PK
(
  WriterId BIGINT NOT NULL,
  PublicationId INTEGER NOT NULL,
  Id INTEGER NOT NULL,
  CONSTRAINT PK_EXTRA_PK PRIMARY KEY (Id),
  CONSTRAINT FK_Writer_Join FOREIGN KEY (WriterId) REFERENCES Writers (Id),
  CONSTRAINT FK_Publication_Join FOREIGN KEY (PublicationId) REFERENCES Publications (Id)
)
;

CREATE TABLE SelfReference
(
  Id INTEGER NOT NULL,
  Title VARCHAR(255) NOT NULL,
  CONSTRAINT PK_SelfReference PRIMARY KEY (Id),
  CONSTRAINT FK_SelfReference FOREIGN KEY (Id) REFERENCES SelfReference (Id)
)
;

ALTER TABLE Writers ADD CONSTRAINT FK_Writers_Publication FOREIGN KEY (Publication_Id) REFERENCES Publications (Id);

-- Indexes
CREATE INDEX IDX_B_Writers ON Writers(LastName, FirstName)
;
CREATE INDEX IDX_A_Writers ON Writers(City, State, PostalCode, Country)
;
CREATE INDEX IDX_A1_Writers ON Writers(City, State)
;
CREATE UNIQUE INDEX IDX_U_Writers ON Writers(Email1, Country)
;
