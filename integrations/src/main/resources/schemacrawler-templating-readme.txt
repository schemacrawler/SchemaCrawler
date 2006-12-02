SchemaCrawler - Templating Integration

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

--- Command Options ---

-command=<command>
	Required, where <command> is one of:
    brief_schema
        If you only want to see table, view and procedure names
    basic_schema
        For more details of tables, views and procedures, 
        including columns and primary keys
    verbose_schema
        For the most detail of the schema, including data 
        types, indexes, foreign keys, and view and 
        procedure definitions
    maximum_schema
        For maximum possible detail of the schema, including
        privileges, and details of privileges, triggers,
        and check constraints
        
--- Output Options ---

-outputformat=<outputformat>
	Where <outputformat> is the file path or class path to the Velocity 
	template

-outputfile=<outputfile>
	Optional - <outputfile> is the path to the output file
