CREATE DATABASE duplicate_names;
USE duplicate_names;

CREATE TABLE alfa (
  uno INT NOT NULL,
  due VARCHAR(45) NULL,
  PRIMARY KEY (uno),
  CONSTRAINT vincolo UNIQUE (due)
);

CREATE TABLE beta (
  uno INT NOT NULL,
  due VARCHAR(45) NULL,
  PRIMARY KEY (uno),
  CONSTRAINT vincolo UNIQUE (due)
);
