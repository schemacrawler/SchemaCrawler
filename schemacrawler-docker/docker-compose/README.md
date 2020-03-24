# SchemaCrawler Tests with Docker Compose 

## Pre-requisites

- Copy the `_testdb` folder from the distribution into the `docker-compose` directory
- Download JDBC drivers for Oracle and IBM DB2 into `_testdb/lib`
- Start a command-prompt in the `docker-compose` directory

## PostgreSQL

### Setup

- To start SchemaCrawler with PostgreSQL, run
  `docker-compose -f schemacrawler.yml -f postgresql.yml up -d`
- Start SchemaCrawler bash with
	`docker exec -it docker-compose_schemacrawler_1 /bin/bash`
- Create a test PostgreSQL database schema, run
	`./createtestschema.sh --url "jdbc:postgresql://postgresql:5432/schemacrawler?ApplicationName=SchemaCrawler;loggerLevel=DEBUG" --user schemacrawler --password schemacrawler`
- Run SchemaCrawler from SchemaCrawler bash
	`schemacrawler --server postgresql --host postgresql --database schemacrawler --user schemacrawler --password schemacrawler --info-level minimum -c list`

### Tear Down

- To stop SchemaCrawler with PostgreSQL, run
  `docker-compose -f schemacrawler.yml -f postgresql.yml down -t 2`
	
	
	
## IBM DB2

> Not working - cannot connect?

### Setup

- To start SchemaCrawler with IBM DB2, run
  `docker-compose -f schemacrawler.yml -f db2.yml up -d`
- Start SchemaCrawler bash with
	`docker exec -it docker-compose_schemacrawler_1 /bin/bash`
- Download IBM DB2 JDBC driver
  ```sh
  cd _testdb/lib
  wget https://search.maven.org/remotecontent?filepath=com/ibm/db2/jcc/11.5.0.0/jcc-11.5.0.0.jar
  cd ../..
  ```
- Create a test IBM DB2 database schema, run
	`./createtestschema.sh --url "jdbc:db2://db2:50000/schcrwlr:retrieveMessagesFromServerOnGetMessage=true;" --user schcrwlr --password schemacrawler`
- Run SchemaCrawler from SchemaCrawler bash
	`schemacrawler --server postgresql --host postgresql --database schcrwlr --user schcrwlr --password schemacrawler --info-level minimum -c list`

Connect to the IBM DB2 container if needed, run
`docker exec -it docker-compose_db2_1 /bin/bash`	

### Tear Down

- To stop SchemaCrawler with IBM DB2, run
  `docker-compose -f schemacrawler.yml -f db2.yml down -t 2`