# Explore the AdventureWorks Database with SchemaCrawler

### Setup

- Start SchemaCrawler with the AdventureWorks Database on Microsoft SQL Server  
  `docker-compose -f adventureworks.yaml up -d`
- Start SchemaCrawler bash with  
  `docker exec -it schemacrawler /bin/bash`


## Tutorial

- List all the tables in the database  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level minimum --command list`
- Create an entity-relationship diagram of the database  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --output-file share/adventureworks.pdf`
- Explore the "Employee" table in detail  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r  --no-info --info-level maximum --command details --grep-tables ".*\.Employee"`
- See table relationships in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-tables ".*\.Employee" --output-file share/employee-table.pdf`
- See child table relationships in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-tables ".*\.Employee" --children 1 --output-file share/employee-table-children.pdf`
- Find all tables with a "BusinessEntityID" column in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-columns .*\.BusinessEntityID --output-file share/businessentityid-tables.pdf`
- Guess at weak associations in a diagram  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command schema --grep-tables ".*\.Employee" --children 1 --weak-associations --output-file share/businessentityid-tables-weak-associations.pdf`
- Find schema design problems with lint  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command lint --grep-tables ".*\.Employee" --children 1`
- See row counts employee related tables in the database  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command count --grep-tables ".*\.Employee" --children 1`
- See data in "Employee" table  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level standard --command dump --grep-tables ".*\.Employee" --output-file share/employee-data.html`
- Output schema to HTML, with diagram, in a single file  
  `schemacrawler --server sqlserver --host adventureworks --database AdventureWorks --schemas AdventureWorks\.[A-Z].* --user SA --password Schem#Crawl3r --info-level maximum --command details --grep-tables ".*\.Employee" --children 1 --output-format htmlx --output-file share/employee-table.html`


### Tear Down

- Stop the SchemaCrawler and database Docker containers  
  `docker-compose -f schemacrawler.yml -f adventureworks.yml down -t0`
