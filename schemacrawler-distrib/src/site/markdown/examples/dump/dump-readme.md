# SchemaCrawler - Dump Example

## Description
The dump example shows how to dump the contents of a database in a diff-able format.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the dump example directory 
4. Run `dump.cmd` (or `dump.sh` on Unix) 

## How to Experiment
1. Try using grep options to include certain tables. For example, try using a command-line option of `-grepcolumns=.*\\.AUTHOR.*`
