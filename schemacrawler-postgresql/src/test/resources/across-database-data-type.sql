create schema schema1;

create type schema1.test_udt_type as (name varchar(50));
create domain schema1.name_type as varchar(20);
create type schema1.sex_type as enum ('male', 'female', 'unspecified');
create domain schema1.date as date;
create type schema1.int2 as (age int2);

create table schema1.nametable
(
  id int primary key not null,
  firstname schema1.test_udt_type not null,
  lastname schema1.name_type not null,
  sex schema1.sex_type not null,
  birthdate schema1.date not null,
  age schema1.int2 not null
);


create schema schema2;

create type schema2.test_udt_type as (age int2);

create table schema2.agetable
(
  id int primary key not null,
  name schema1.test_udt_type not null,
  age schema2.test_udt_type not null
);
