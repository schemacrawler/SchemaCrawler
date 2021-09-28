<!-- markdownlint-disable MD024 -->
# SchemaCrawler Tests with Docker Compose

## Pre-requisites


## Microsoft SQL Server

### Setup

- To start SchemaCrawler with Microsoft SQL Server, run
  `docker-compose -f adventureworks.yml up -d`
- Start SchemaCrawler bash with
  `docker exec -it schemacrawler /bin/bash`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks2019 --schemas AdventureWorks2019\.[A-Z].* --user SA --password ThisIsNotASecurePassword123 --info-level minimum -c list --table-types TABLE`
- Output can be created with `--output-file output/out.txt`

### Tear Down

- To stop SchemaCrawler with Microsoft SQL Server, run
  `docker-compose -f adventureworks.yml down -t0`

