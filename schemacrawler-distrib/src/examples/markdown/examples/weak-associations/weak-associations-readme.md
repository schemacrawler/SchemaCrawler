# SchemaCrawler - Weak Associations Example

## Description
SchemaCrawler allows you to provide information about weak associations, or references from 
a column in one table to another table in a YAML file 
with the `--attributes-file` command-line switch.

## How to Setup
1. Make sure that SchemaCrawler is [installed on your system](https://www.schemacrawler.com/downloads.html)
2. Make sure that `schemacrawler` is on your PATH

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `weak-associations` example directory 
4. Run `weak-associations.cmd weak-associations.yaml weak-associations.png` (or `weak-associations.sh weak-associations.yaml weak-associations.png` on Unix). 
5. View the image in `weak-associations.png` to see the weak associations that were loaded from the YAML file

## How to Experiment
- Modify `weak-associations.yaml` and rerun the command
