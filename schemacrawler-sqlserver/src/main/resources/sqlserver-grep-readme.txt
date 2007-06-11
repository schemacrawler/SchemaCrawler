SchemaCrawler Grep is a command-line tool to search for tables that contain
columns matching regular expressions in your database schema.

SchemaCrawlerGrep [options]

--- Connection Options ---

-host=<host>
	Host name. Optional, defaults to localhost.
	
-port=<port>
	Port number. Optional, defaults to 1433.	

-database=<database>
	Database name.
	
-user=<user>
	Database user name.

-password=<password>
	Database password.
	

--- Grep Options ---
	
-tables=<regular-expression>
	Optional, where <regular-expression> is a regular expression to match table 
	names.
	For example, 
	-tables C.*|P.*
	matches any table whose names start with C or P.
	
-columns=<regular-expression>
	Optional, where <regular-expression> is a regular expression to match column 
	names in the form "TABLENAME.COLUMNNAME".
	For example,
	-columns .*\.STREET|.*\.PRICE
	matches columns named STREET or PRICE in any table.
	
-v (short for -invert-match)        
	Inverts the match, and shows non-matching tables and columns.	
