#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.options.DatabaseServerType;

public final class NewDBDatabaseConnector
  extends DatabaseConnector
{
	
  private static final Logger LOGGER = Logger.getLogger(NewDBDatabaseConnector.class.getName());
	  
  public NewDBDatabaseConnector()
  {
    super(new DatabaseServerType("newdb", "NewDB"),
          "/help/Connections.newdb.txt",
          "/schemacrawler-newdb.config.properties",
          "/newdb.information_schema");
	// SchemaCrawler will control output of log messages if you use JDK logging
	LOGGER.log(Level.INFO, "Loaded pluging for newdb");    
  }

}
