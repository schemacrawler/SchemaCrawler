SchemaCrawler is a platform (OS and DB) independent command-line tool to output
your database schema and data in a readable form. The output is designed to be
diff-ed with previous versions of your database schema.
		
sc [options]

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
	
-schemapattern=<schemapattern>
	Optional, specifies the schema to use. Use _ to match a single character, 
	and % to match many characters.
	
-schemapattern=<schemapattern>
	Optional, specifies the schema to use. Use _ to match a single character, 
	and % to match many characters.
	
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
