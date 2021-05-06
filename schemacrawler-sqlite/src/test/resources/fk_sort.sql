CREATE TABLE persons (
  id int PRIMARY KEY,
  name varchar,
  city_code int,
  country_code int,
  manager int,
  customer int,
  FOREIGN KEY (customer) REFERENCES customers(id),
  FOREIGN KEY (manager) REFERENCES persons(id),
  FOREIGN KEY (country_code) REFERENCES countries(code),  
  FOREIGN KEY (city_code) REFERENCES cities(code)
);

CREATE TABLE cities (
  code int PRIMARY KEY,
  name varchar,
  state_code varchar
);

CREATE TABLE countries (
  code int PRIMARY KEY,
  name varchar,
  continent_code varchar
);

CREATE TABLE customers (
  id int PRIMARY KEY,
  person int,
  FOREIGN KEY (person) REFERENCES persons(id)
);

CREATE TABLE employees (
  id int PRIMARY KEY,
  person int,
  FOREIGN KEY (person) REFERENCES persons(id)
);
