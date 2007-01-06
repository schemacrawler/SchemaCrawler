SchemaCrawler Grep is a command-line tool to search for tables that contain
columns matching regular expressions in your database schema.

To use SchemaCrawler Grep:
1. Create a working directory, and copy the program jar file to that
   directory.
2. Copy your database driver jar or zip files to the same directory.
3. Modify schemacrawler.config.properties to point to your database.
4. Try not to give your database user DBA permissions - at least when you
   are running SchemaCrawler Grep.
5. Start a command shell, and cd to the working directory.


java -cp <schemacrawler-jar> schemacrawler.Grep [options]

--- Optional Configuration Options ---

-g <config-file> (short for -configfile <config-file>)
    Reads SchemaCrawler configuration properties from <config-file> instead
    of the default schemacrawler.config.properties

-p <config-override-file> (short for -configoverridefile <config-override-file>)
    Reads SchemaCrawler configuration properties from <config-override-file>
    and overrides the properties from the configuration file

--- Required Connection Options ---

One of:

-c <connection_name> (short for -connection <connection_name>)
    Uses a named connection
    
-d (short for -default)
    Uses the default connection
    
-x <connection_name> (short for -prompt <connection_name>)
    Prompts for connection information, which is saved
    into <connection_name>.properties
    
Or, all of the following:

-driver=<driver-class-name>
	Fully qualified name of the JDBC driver class.
	
-url=<url>
	JDBC connection URL to the database.
	
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
		
