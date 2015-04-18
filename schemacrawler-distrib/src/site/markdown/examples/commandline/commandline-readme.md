# SchemaCrawler - Commandline Example

## Description
The command example demonstrates the use of SchemaCrawler from the shell command-line.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the SchemaCrawler distribution directory, that is, the `_schemacrawler` directory. 
4. Run `sc.cmd -help` (or `sc.sh -help` on Unix), to give you a list of available command-line options 
5. To start with, run `sc.cmd -server=hsqldb -database=schemacrawler -password= -infolevel=minimum -command=list` 
   (use `sc.sh` instead of `sc.cmd` on Unix)

## How to Experiment
1. Take a look at the SchemaCrawler grep example, in the [grep](../grep/grep-readme.html) example directory. 
2. Take a look at the SchemaCrawler lint example, in the [lint](../lint/lint-readme.html) example directory. 
3. Take a look at the SchemaCrawler offline snapshot example, in the [offline](../offline/offline-readme.html) example directory. 
3. Try using different SchemaCrawler command-line options.
4. Try running an arbitrary query, with a query command `"-command=SELECT * FROM PUBLIC.BOOKS.AUTHORS"`. 
   (The quotes are required.) 
5. Redirect the output of SchemaCrawler into a file, by adding ` > file.txt` to the command-line. 
6. Try defining a new database connection, using your favorite database and driver. 
   Use the `-url` command-line option.
