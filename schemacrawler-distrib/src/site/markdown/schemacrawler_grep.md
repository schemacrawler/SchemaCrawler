# SchemaCrawler Grep

SchemaCrawler is a command-line tool that allows you to search your database
for tables and columns that match a regular expression, much like the standard
[grep](http://en.wikipedia.org/wiki/Grep) tool.

The SchemaCrawler command-line allows filtering tables, views, columns, stored
procedure and function based on regular expressions, in addition to the grep
functionality. This can be useful, say, when you want to search through your
schema to find all tables that have a CUSTOMER_ID column, for example.

To find tables with certain names, run SchemaCrawler with command-line options
similar to 
`-infolevel=standard -command=list -grepcolumns=.*\\..*CUSTOMER.*\\..*` 
This will find table with names that have "CUSTOMER" in them.

To find tables with certain column names, run SchemaCrawler with command-line
options similar to 
`-infolevel=standard -command=list -grepcolumns=.*\\.CUSTOMER_ID` 
This will find all tables that have a CUSTOMER_ID column.

For more details, see the grep example in the 
[SchemaCrawler examples](http://github.com/sualeh/SchemaCrawler/releases/) 
download.
