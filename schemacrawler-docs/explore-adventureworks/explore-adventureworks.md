# Explore the AdventureWorks Database with SchemaCrawler

## Setup

- Start SchemaCrawler with the AdventureWorks Database on Microsoft SQL Server
  `docker compose -f adventureworks.yaml up -d`
- Start SchemaCrawler bash with
  `docker exec -it schemacrawler bash`


## Exploration

- List all the tables in the database
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level minimum --command list`
- Create an entity-relationship diagram of the database
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command schema --output-file share/adventureworks.pdf`
- Explore the "Employee" table in detail
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler  --no-info --info-level maximum --command details --grep-tables ".*\.\"Employee\""`
- See table relationships in a diagram
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command schema --grep-tables ".*\.\"Employee\"" --output-file share/employee-table.pdf`
- See child table relationships in a diagram
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command schema --grep-tables ".*\.\"Employee\"" --children 1 --output-file share/employee-table-children.pdf`
- Show variations of the diagram
  - Tables only with `--table-types table`
  - Portable names with `--portable=names` - schema colors are preserved
  - Diagram title with `--title "Employee and Related Tables"`
  - Show only important columns with `--command brief`
  - Show indexes on the diagram with `--command details`
- Find all tables with a "BusinessEntityID" column in a diagram
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command schema --grep-columns .*\.\"+BusinessEntityID\"+ --output-file share/businessentityid-tables.pdf`
- Show advanced diagram configuration by editing the configuration file
  - Hide foreign key names with `schemacrawler.graph.show.foreignkey.cardinality=false`
  - Show ordinal numbers with `schemacrawler.format.show_ordinal_numbers=true`


## Offline Access

- Serialize schema metadata model for offline access
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level maximum --command serialize --output-format ser --output-file share/adventureworks-schema.ser`
- Use offline database
  `schemacrawler --server offline --database share/adventureworks-schema.ser --info-level maximum --command list`


## Model Your Existing Database

- Generate and edit a dbdiagram.io diagram
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level maximum --command script --grep-tables ".*\.\"Employee\"" --children 1 --scripting-language python --script dbml.py --output-file share/adventureworks.dbml`
- Generate and edit a mermaid diagram
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level maximum --command script --grep-tables ".*\.\"Employee\"" --children 1 --scripting-language python --script mermaid.py --output-file share/adventureworks.mermaid`


## Other Commands

- Guess at weak associations in a diagram
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command schema --grep-columns .*\.\"+BusinessEntityID\"+ --weak-associations --output-file share/businessentityid-tables-weak-associations.pdf`
- Find schema design problems with lint
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command lint --grep-tables ".*\.\"Employee\"" --children 1`
- See row counts employee related tables in the database
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command count --grep-tables ".*\.\"Employee\"" --children 1`
- Show row counts on the diagram with "--row-counts" - `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command schema --grep-tables ".*\.\"Employee\"" --row-counts --children 1 --output-file share/employee-table-children.pdf`
- See data in "Employee" table
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level standard --command dump --grep-tables ".*\.\"Employee\"" --output-file share/employee-data.html`
- Output schema to HTML, with diagram, in a single file
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level maximum --command details --grep-tables ".*\.\"Employee\"" --children 1 --output-format htmlx --output-file share/employee-table.html`
- Get JSON output
  `schemacrawler --server postgresql --host adventureworks --database postgres --user schemacrawler --password schemacrawler --info-level maximum --command serialize --grep-tables ".*\.\"Employee\"" --children 1 --output-format json --output-file share/employee-table.json`


## Tear Down

- Exit SchemaCrawler bash with
  `exit`
- Stop the SchemaCrawler and database Docker containers
  `docker compose -f adventureworks.yaml down -t0`
