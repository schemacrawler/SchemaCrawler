SchemaCrawler is a platform (OS and DB) independent command-line tool to output
your database schema and data in a readable form. The output is designed to be
diff-ed with previous versions of your database schema.

To use SchemaCrawler:
1. Create a working directory, and copy the program jar file to that
   directory.
2. Copy your database driver jar or zip files to the same directory.
3. Modify schemacrawler.config.properties to point to your database.
4. Try not to give your database user DBA permissions - at least when you
   are running SchemaCrawler.
5. Start a command shell, and cd to the working directory.


java -jar <schemacrawler-jar> [options]

--- Optional Configuration Options ---

-g <config-file> (short for -configfile <config-file>)
    Reads SchemaCrawler configuration properties from <config-file> instead
    of the default schemacrawler.config.properties

-p <config-override-file> (short for -configoverridefile <config-override-file>)
    Reads SchemaCrawler configuration properties from <config-override-file>
    and overrides the properties from the configuration file

-log_level <log_level>
		Log level - may be one of: 
		OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
		
--- Required Connection Options ---

One of:

-c <connection_name> (short for -connection <connection_name>)
    Uses a named connection
    
-d (short for -default)
    Uses the default connection
    
-x <connection_name> (short for -prompt <connection_name>)
    Prompts for connection information, which is saved
    into <connection_name>.properties
    
-a (short for -testall)
    Tests all the connections defined in the configuration properties file,
    but does not execute any commands

Or, all of the following:

-driver=<driver-class-name>
	Fully qualified name of the JDBC driver class.
	
-url=<url>
	JDBC connection URL to the database.
	
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
    truncate
      To truncate the tables
    drop
      To drop the tables
    dump
    	To select all rows from the tables, and output the data
    <query_name>
	    Query name, as specified in the configuration properties file
			The query itself can contain the variables ${table} and ${tabletype}
			or system properties referenced as ${<system-property-name>}.
			Queries without any variables are executed exactly once. Queries
			with variables are executed once for each table, with the variables
			substituted.

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
