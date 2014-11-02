# SchemaCrawler - Grep Example

## Description
The grep example shows how to search a schema for tables and columns matching
a regular expression.

## How to Run
1. Follow the instructions in the [commandline](../commandline/commandline-readme.html) example. 
2. To find tables with certain column names, run `sc.cmd -infolevel=standard -command=details -routines= -grepcolumns=.*\\.PUBLISHER` 
   (or `sc.sh -infolevel=standard -command=details -routines= -grepcolumns=.*\\.PUBLISHER` on Unix) 
3. To find routines with certain parameter names, run `sc.cmd -infolevel=standard -command=schema -tables= -grepinout=.*\\.B_OFFSET` 
   (or `sc.sh -infolevel=standard -command=schema -tables= -grepinout=.*\\.B_OFFSET` on Unix) 

## How to Experiment
1. Try grep for columns in tables that match a pattern.
