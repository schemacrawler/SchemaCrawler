# SchemaCrawler - Dump Example

## Description
The dump example shows how to dump the contents of a database in a diff-able format.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the dump example directory 
4. Run `dump.cmd dump.html` (or `dump.sh dump.html` on Unix) 

## How to Experiment
1. Try using grep options to include certain tables. For example, try using a command-line option of `--grep-columns=.*\\.AUTHOR.*`
