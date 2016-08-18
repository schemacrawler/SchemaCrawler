# SchemaCrawler - Database-Specific Queries Example

## Description
SchemaCrawler has the capability to execute any arbitrary SQL, even if the SQL
is database-specific. This example demonstrates how to execute arbitrary SQL.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the db-specific-query example directory. 
4. Run `db-specific-query.cmd` (or `db-specific-query.sh` on Unix). 

## How to Experiment
1. Try generating different output formats, such as HTML or JSON. 
2. Try modifying `config/schemacrawler.config.properties` with different queries. 
3. Redirect the output of SchemaCrawler into a file, by running `db-specific-query.cmd > file.txt`. 
