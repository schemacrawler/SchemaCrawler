# SchemaCrawler - User Defined Queries Example

## Description

SchemaCrawler has the capability to execute any arbitrary SQL, per table. The
following variables are available to aid in the construction of queries -
`${tabletype}`, `${table}`, and `${columns}`. This example demonstrates how to
execute arbitrary SQL, per table.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the user-defined-query example directory 
4. Run `user-defined-query.cmd` (or `user-defined-query.sh` on Unix) 

## How to Experiment
1. Try generating different output formats, such as HTML or JSON. 
2. Try modifying the `schemacrawler.config.properties`` with different queries. 
3. Redirect the output of SchemaCrawler into a file, by running `user-defined-query.cmd > file.txt`. 
4. Try running an arbitrary query, following the instructions in the [commandline](../commandline/commandline-readme.html) example. 
