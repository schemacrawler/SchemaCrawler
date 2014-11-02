# SchemaCrawler - Commandline Example

## Description
The command example demonstrates the use of SchemaCrawler from the shell command line.

## How to Run
1. Make sure that java is on your PATH
2. Start the database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the `commandline` example directory. 
4. Run `sc.cmd -help` (or `sc.sh -help` on Unix), to give you a list of available command line options 
5. To start with, run `sc.cmd -infolevel=minimum -command=brief` (or `sc.sh -infolevel=minimum -command=brief` on Unix); 
typically, connection parameters would need to provided, but in the examples, these are built into the `sc.cmd` and `sc.sh` scripts 

## How to Experiment
1. Take a look at the SchemaCrawler grep example, in the [grep](../grep/grep-readme.html) example directory. 
2. Take a look at the SchemaCrawler lint example, in the [lint](../lint/lint-readme.html) example directory. 
3. Try using different SchemaCrawler command line options.
4. Try running an arbitrary query, with a query command `"-command=SELECT * FROM PUBLIC.BOOKS.AUTHORS"`. 
   (The quotes are required.) 
5. Redirect the output of SchemaCrawler into a file, by running `sc.cmd > file.txt`. 
6. Try defining a new database connection, using your favorite database and driver. 
