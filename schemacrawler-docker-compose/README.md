<!-- markdownlint-disable MD024 -->
# SchemaCrawler Tests with Docker Compose

## Pre-requisites

- Run a full SchemaCrawler build with `mvn -Ddistrib clean package`
- Or, for an incremental build, run `mvn -Ddistrib clean package` for the "schemacrawler-distrib", "schemacrawler-docker" and "schemacrawler-docker-compose" submodules in order



## PostgreSQL

### Setup

- To start SchemaCrawler with PostgreSQL, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f postgresql.yaml up -d`
- Create a test PostgreSQL database schema, run
  `docker exec -it schemacrawler ./testdb/createtestschema.sh --url "jdbc:postgresql://postgresql:5432/schemacrawler?ApplicationName=SchemaCrawler;loggerLevel=DEBUG" --user schemacrawler --password schemacrawler --debug`

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --server postgresql --host postgresql --database schemacrawler --user schemacrawler --password schemacrawler --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

### Tear Down

- To stop SchemaCrawler with PostgreSQL, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f postgresql.yaml down -t0`



## Oracle

### Setup

- To start SchemaCrawler with Oracle, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f oracle.yaml up -d`
- Create a test Oracle database schema, run
  `docker exec -it schemacrawler ./testdb/createtestschema.sh --url "jdbc:oracle:thin:@//oracle:1521/xepdb1" --user "SYS AS SYSDBA" --password test --scripts-resource /oracle.scripts.txt --debug`

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --server oracle --host oracle --database xepdb1 --user "SYS AS SYSDBA" --password test --schemas BOOKS --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

### Tear Down

- To stop SchemaCrawler with Oracle, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f oracle.yaml down -t0`



## Microsoft SQL Server

### Setup

- To start SchemaCrawler with Microsoft SQL Server, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f sqlserver.yaml up -d`
- Create a test Microsoft SQL Server database schema, run
  `docker exec -it schemacrawler ./testdb/createtestschema.sh --url "jdbc:sqlserver://sqlserver:1433;databaseName=master;encrypt=false" --user SA --password Schem#Crawl3r --debug`

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --server sqlserver --host sqlserver --database BOOKS --schemas BOOKS\.dbo --user SA --password Schem#Crawl3r --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

### Tear Down

- To stop SchemaCrawler with Microsoft SQL Server, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f sqlserver.yaml down -t0`



## MySQL

### Setup

- To start SchemaCrawler with MySQL, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f mysql.yaml up -d`
- Create a test MySQL database schema, run
  `docker exec -it schemacrawler ./testdb/createtestschema.sh --url "jdbc:mysql://mysql:3306/books?disableMariaDbDriver&useInformationSchema=true" --user root --password schemacrawler --debug`

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --server mysql --host mysql --database books --user schemacrawler --password schemacrawler --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

### Tear Down

- To stop SchemaCrawler with MySQL, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f mysql.yaml down -t0`



## IBM DB2


### Setup

- To start SchemaCrawler with IBM DB2, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f db2.yaml up -d`
- Create a test IBM DB2 database schema, run
  `docker exec -it schemacrawler ./testdb/createtestschema.sh --url "jdbc:db2://db2:50000/schcrwlr:retrieveMessagesFromServerOnGetMessage=true;" --user schcrwlr --password schemacrawler --debug`

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --server db2 --host db2 --database schcrwlr --schemas SCHCRWLR --user schcrwlr --password schemacrawler --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

Connect to the IBM DB2 container if needed, run
`docker exec -it db2 bash`

### Tear Down

- To stop SchemaCrawler with IBM DB2, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f db2.yaml down -t0`



## Cassandra


### Setup

- To start SchemaCrawler with Cassandra, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f cassandra.yaml up -d`
- Create a test Cassandra database schema, run
  `docker exec -it cassandra cqlsh -f /testdb/create-cassandra-database.cql`

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --url jdbc:cassandra://cassandra:9042/books --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

Connect to the Cassandra container if needed, run
`docker exec -it cassandra bash`

### Tear Down

- To stop SchemaCrawler with Cassandra, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f cassandra.yaml down -t0`



## Trino


### Setup

- To start SchemaCrawler with Trino, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f trino.yaml up -d`
- The test database is created in the Docker container

### Testing

- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --url jdbc:trino://trino:8080/tpch --user test --schemas tpch.sf100 --info-level minimum -c list`
- Output can be created with `--output-file share/out.txt`

Connect to the Trino container if needed, run
`docker exec -it trino bash`

### Tear Down

- To stop SchemaCrawler with Trino, run
  `docker-compose -f schemacrawler.yaml -f dbeaver.yaml -f trino.yaml down -t0`



# Diagrams

## PlantUML

Generate PlantUML from a database:

```sh
docker run \
--mount type=bind,source="$(pwd)",target=/home/schcrwlr/share \
--rm -it \
schemacrawler/schemacrawler \
/opt/schemacrawler/bin/schemacrawler.sh \
--server=sqlite \
--database=sc.db \
--info-level=standard \
--command script \
--title "Database Schema" \
--script-language python \
--script plantuml.py \
--output-file share/schema.puml
```

Generate a diagram:

```sh
docker run \
--mount type=bind,source="$(pwd)",target=/data \
--rm -it \
plantuml/plantuml \
-tpng \
-v \
/data/schema.puml
```
