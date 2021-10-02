<!-- markdownlint-disable MD024 -->
# Explore a New Database with SchemaCrawler

### Setup

- Download an SQLite version of the [Sakila database](https://dev.mysql.com/doc/sakila/en/)
  `wget -N -q --show-progress https://github.com/ivanceras/sakila/raw/master/sqlite-sakila-db/sakila.db`
- To start SchemaCrawler, run
  `docker run -v "$(pwd)":/home/schcrwlr/share --name schemacrawler --rm -i -t --entrypoint=/bin/bash schemacrawler/schemacrawler:v16.15.7`
- Run SchemaCrawler from Docker container bash
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --info-level minimum -c list --table-types TABLE`
- Output can be created with `--output-file output/out.txt`

### Tear Down

- To stop the SchemaCrawler Docker container, run
  `docker-compose -f schemacrawler.yml down -t0`
