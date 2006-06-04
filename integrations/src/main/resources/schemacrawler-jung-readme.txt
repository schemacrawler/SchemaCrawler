SchemaCrawler - JUNG Graphing Integration

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
        If you only want to see table and view names
    basic_schema
        For more details of tables and views
    verbose_schema
        For most details of tables and views, including keys
    maximum_schema
        For maximum possible detail of the schema
                
--- Output Options ---

-outputformat=<outputformat>
	Where <outputformat> is the size of the graph, in pixels - for
	example, 800x600

-outputfile=<outputfile>
	Optional - <outputfile> is the path to the JPEG output file
