#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseServerType;

public final class NewDBDatabaseConnector
  extends DatabaseConnector
{

  private static final Logger LOGGER = Logger
    .getLogger(NewDBDatabaseConnector.class.getName());

  public NewDBDatabaseConnector()
  {
    super(new DatabaseServerType("newdb", "NewDB"),
          "/help/Connections.newdb.txt",
          "/schemacrawler-newdb.config.properties",
          "/newdb.information_schema",
          "jdbc:newdb:.*");
    // SchemaCrawler will control output of log messages if you use JDK logging
    LOGGER.log(Level.INFO, "Loaded plugin for NewDB");
  }
  
}
