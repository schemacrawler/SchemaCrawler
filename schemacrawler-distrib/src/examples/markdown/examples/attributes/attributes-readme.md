# SchemaCrawler - Attributes Example

## Description
SchemaCrawler allows you to provide table and column remarks from a YAML file 
with the `--attributes-file` command-line switch.

## How to Setup
1. Make sure that SchemaCrawler is [installed on your system](https://www.schemacrawler.com/downloads.html)
2. Make sure that `schemacrawler` is on your PATH

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `attributes` example directory 
4. Run `attributes.cmd attributes.yaml` (or `attributes.sh attributes.yaml` on Unix). 

## How to Experiment
- Look at the `weak-associations` example to see how you can add weak associations to the schema
