# SchemaCrawler - PostgreSQL Dump Example

## Description
SchemaCrawler allows loading PostgreSQL database dumps. This example shows how to load a PostgreSQL database dump and run SchemaCrawler commands against it.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `_downloader` directory 
3. Run `download.cmd postgresql-embedded` (or `download.sh postgresql-embedded` on Unix) to
   install PostgreSQL support
4. Run `postgresql-embedded.cmd` (or `postgresql-embedded.sh` on Unix) to download the PostgreSQL database software

## How to Run
1. Start a command shell in the `postgresql` example directory
2. Run `ruby.cmd tables.rb` (or `ruby.sh tables.rb` on Unix) 

## How to Experiment
1. Run different SchemaCrawler commands.
