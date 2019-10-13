# SchemaCrawler - Serialization Example

## Description
SchemaCrawler allows export of database schema to JSON and YAML, via the `serialize` command.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `_downloader` directory 
3. Run `download.cmd jackson` (or `download.sh jackson` on Unix) to
   install serialization support using Jackson

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `serialize` example directory 
4. Run `serialize.cmd json` (or `serialize.sh json` on Unix) 

## How to Experiment
1. Run `serialize.cmd yaml` (or `javascript.sh yaml` on Unix). 
   (In order to restore the database, restart the database server.) 
