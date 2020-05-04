CREATE TABLE address (
    address_id INT AUTO_INCREMENT,
    name varchar(255) not null ,
    PRIMARY KEY (address_id)
);


CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT,
    name varchar(255) not null ,
    address_id int,
    primary key(customer_id),
    foreign key (address_id) references address(address_id) ON delete cascade
);

CREATE USER AWM PASSWORD 'awm';
GRANT ALL ON address TO AWM;
