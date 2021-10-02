<!-- markdownlint-disable MD024 -->
# Explore a New Database with SchemaCrawler

### Setup

- Download an SQLite version of the [Sakila database](https://dev.mysql.com/doc/sakila/en/)
  `wget -N -q --show-progress https://github.com/ivanceras/sakila/raw/master/sqlite-sakila-db/sakila.db`
- To start SchemaCrawler, run
  `docker run -v "$(pwd)":/home/schcrwlr/share --name schemacrawler --rm -i -t --entrypoint=/bin/bash schemacrawler/schemacrawler:v16.15.7`


## Tutorial

- List all the tables in the database
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --info-level minimum --command list`
- List only film related tables in the database
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --info-level minimum --command list --tables film.*`
- List only film related tables in the database
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --info-level minimum --command list --tables film.*`
- Explore the film table in detail
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --no-info --info-level maximum -c details --tables film`
- Output to HTML
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --no-info --info-level maximum -c details --tables film --output-file share/film-table.html`
- See row counts film related tables in the database
  `schemacrawler --url "jdbc:sqlite:share/sakila.db" --info-level minimum --command count --tables film.*`


### Tear Down

- To stop the SchemaCrawler Docker container, run
  `docker-compose -f schemacrawler.yml down -t0`
