#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.options.DatabaseServerType;

public final class NewDBDatabaseConnector
  extends DatabaseConnector
{

  public NewDBDatabaseConnector()
  {
    super(new DatabaseServerType("newdb", "NewDB"),
          "/help/Connections.newdb.txt",
          "/schemacrawler-newdb.config.properties",
          "/newdb.information_schema");
  }

}
