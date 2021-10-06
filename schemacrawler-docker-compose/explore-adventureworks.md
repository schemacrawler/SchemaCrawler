<!-- markdownlint-disable MD024 -->
# Explore the AdventureWorks Database with SchemaCrawler

### Setup

- Start SchemaCrawler with the AdventureWorks Database on Microsoft SQL Server  
  `docker-compose -f schemacrawler.yml -f adventureworks.yml up -d`
- Start SchemaCrawler bash with  
  `docker exec -it schemacrawler /bin/bash`


## Tutorial

- List all the tables in the database  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level minimum --command list`
- List only film related tables in the database  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level minimum --command list --grep-tables film.*`
- Explore the "film" table in detail  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r  --no-info --info-level maximum --command details --grep-tables film`
- See table relationships in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-tables film --output-file share/film-table.pdf`
- See child table relationships in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-tables film --children 1 --output-file share/film-table-children.pdf`
- Find all tables with a "film_id" column in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-columns .*\.film_id --output-file share/film_id_tables.pdf`
- Guess at weak associations in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-columns .*\.film_id --weak-associations --output-file share/film-table-weak-associations.pdf`
- Find schema design problems with lint  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command lint --grep-columns .*\.film_id`
- See row counts film related tables in the database  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level minimum --command count --grep-tables film.*`
- See data in "film" table  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command dump --grep-tables film --output-file share/film-data.html`
- Output schema to HTML, with diagram, in a single file  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level maximum --command details --grep-tables film --output-format htmlx --output-file share/film-table.html`
- Diff with another version of Sakila  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --output-file sakila.txt`  
  `wget -O sakila_master.db https://github.com/bradleygrant/sakila-sqlite3/raw/main/sakila_master.db`  
  `schemacrawler --url "jdbc:sqlite:sakila_master.db" --info-level standard --command schema --output-file sakila_master.txt`
- *TODO*: Open in dbdiagram.io and edit the film text table schema, and generate new scripts

### Tear Down

- To stop the SchemaCrawler Docker container, run
  `docker--commandompose -f schemacrawler.yml down -t0`
