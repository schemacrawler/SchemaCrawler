SchemaCrawler is a platform (OS and DB) independent command-line tool to output
your database schema and data in a readable form. The output is designed to be
diff-ed with previous versions of your database schema.
		
SchemaCrawler [options]

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
	
--- Command Options ---

-command=<command>
	Required, where <command> is a comma-separated list of:
    brief_schema
      If you only want to see table, view and procedure names
    basic_schema
      For more details of tables, views and procedures, including columns 
      and primary keys
    verbose_schema
      For the most detail of the schema, including data types, indexes, 
      foreign keys, and view and procedure definitions
    maximum_schema
      For maximum possible detail of the schema, including privileges, and 
      details of privileges, triggers, and check constraints
    count
      To count rows in the tables
    dump
    	To select all rows from the tables, and output the data

--- Output Options ---

-outputformat=<outputformat>
	Optional, where <outputformat> is one of:
    text
      For text output (default)
    html
      For XHTML output
    csv
      For CSV output

-outputfile=<outputfile>
	Optional - <outputfile> is the path to the output file
