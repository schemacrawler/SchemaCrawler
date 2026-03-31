# SchemaCrawler - Implicit Associations Example

## Description
SchemaCrawler allows you to provide information about Implicit Associations, or references from 
a column in one table to another table in a YAML file 
with the `--attributes-file` command-line switch.

## How to Setup
1. Make sure that SchemaCrawler is [installed on your system](https://www.schemacrawler.com/downloads.html)
2. Make sure that `schemacrawler` is on your PATH

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `implicit-associations` example directory 
4. Run `implicit-associations.cmd implicit-associations.yaml implicit-associations.png` (or `implicit-associations.sh implicit-associations.yaml implicit-associations.png` on Unix). 
5. View the image in `implicit-associations.png` to see the Implicit Associations that were loaded from the YAML file

## How to Experiment
- Modify `implicit-associations.yaml` and rerun the command
