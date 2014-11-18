# SchemaCrawler - Grep Example

## Description
The grep example shows how to search a schema for tables and columns matching
a regular expression.

## How to Run
1. Follow the instructions in the [commandline](../commandline/commandline-readme.html) example. 
2. To find tables with certain column names, run 
   `sc.cmd -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=standard -command=details -routines= -noinfo -grepcolumns=.*\.PUBLISHER` 
   (use `sc.sh` instead of `sc.cmd` on Unix)
3. To find routines with certain parameter names, run 
  `sc.cmd -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=standard -command=schema -tables= -noinfo -grepinout=.*\.B_ADDR` 
   (use `sc.sh` instead of `sc.cmd` on Unix) 

## How to Experiment
1. Try grep for columns in tables that match a pattern.
