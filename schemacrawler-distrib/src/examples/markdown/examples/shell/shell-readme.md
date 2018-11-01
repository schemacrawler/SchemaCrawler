# SchemaCrawler Shell Example

## Description
The shell example demonstrates the use of SchemaCrawler Shell.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `_downloader` directory 
4. Run `download.cmd shell` (or `download.sh shell` on Unix) to  
   install SchemaCrawler Shell support 
5. Copy the `schemacrawler-shell.cmd` and `schemacrawler-shell.sh` files into the  
   the `_schemacrawler` directory
5. Start a command shell in the SchemaCrawler distribution directory,  
   that is, the `_schemacrawler` directory
6. Run `schemacrawler-shell.cmd ` (or `schemacrawler-shell.sh` on Unix)
7. Run the `help` command in SchemaCrawler Shell
8. Run the following commands in SchemaCrawler Shell
```
connect -server hsqldb -user sa -database schemacrawler
load-catalog -infolevel maximum
execute -command details
```

## How to Experiment
1. Try using different SchemaCrawler Shell commands.
