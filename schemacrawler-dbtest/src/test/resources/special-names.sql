CREATE DATABASE `some-new-database`;

CREATE TABLE `some-new-database`.`some-people`
(
  Id INT PRIMARY KEY NOT NULL,
  `some-name` VARCHAR(40) NOT NULL
);

CREATE TABLE `some-new-database`.regular_people
(
  Id INT PRIMARY KEY NOT NULL,
  regular_name VARCHAR(40) NOT NULL
);
