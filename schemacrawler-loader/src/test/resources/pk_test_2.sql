CREATE TABLE TABLE9_PK
(
  ENTITY_ID INTEGER NOT NULL,
  COL12 CHAR(25),
  COL13 VARCHAR(25),
  COL14 NUMERIC NOT NULL,
  COL15 VARCHAR(25),
  PRIMARY KEY (ENTITY_ID)
);

CREATE TABLE TABLE3_FK
(
  ENTITY3_ID INTEGER NOT NULL,
  ENTITY_ID INTEGER NOT NULL,
  COL32 CHAR(25),
  COL33 VARCHAR(25),
  COL34 NUMERIC NOT NULL,
  COL35 VARCHAR(25),
  PRIMARY KEY (ENTITY3_ID)
);
