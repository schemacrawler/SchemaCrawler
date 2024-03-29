# SchemaCrawler - Commandline Example

## Description
The command example demonstrates the use of SchemaCrawler from the shell command-line.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the SchemaCrawler distribution directory, that is, the `_schemacrawler/bin` directory. 
4. Run `schemacrawler.cmd --help` (or `schemacrawler.sh --help` on Unix), to give you a list of available command-line options 
5. To start with, run 
   `schemacrawler.cmd --server=hsqldb --database=schemacrawler --password= --info-level=minimum --command=list` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)
6. Run using a SQLite database with 
   `schemacrawler.cmd --server=sqlite --database=../_testdb/sc.db --password= --info-level=minimum --command=list` 
   (use `schemacrawler.sh` instead of `schemacrawler.cmd` on Unix)
   
## How to Experiment
1. Take a look at the SchemaCrawler grep example, in the [grep](../grep/grep-readme.html) example directory. 
2. Take a look at the SchemaCrawler lint example, in the [lint](../lint/lint-readme.html) example directory. 
3. Take a look at the SchemaCrawler offline snapshot example, in the [offline](../offline/offline-readme.html) example directory. 
4. Take a look at the SchemaCrawler Interactive Shell example, in the [interactive](../interactive/interactive-readme.html) example directory.
5. Try using different SchemaCrawler command-line options.
6. Try modifying `config/schemacrawler.config.properties` with different options. 
7. Try running an arbitrary query, with a query command `"--command=SELECT * FROM PUBLIC.BOOKS.AUTHORS"`. 
   (The quotes are required.) 
8. Redirect the output of SchemaCrawler into a file, by adding `> file.txt` to the command-line. 
9. Try defining a new database connection, using your favorite database and driver. 
   Use the `-url` command-line option.
