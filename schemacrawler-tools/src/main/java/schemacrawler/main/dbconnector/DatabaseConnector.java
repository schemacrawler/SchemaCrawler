package schemacrawler.main.dbconnector;


import java.sql.Connection;

/**
 * Data-source parser.
 * 
 * @author sfatehi
 */
public interface DatabaseConnector
{

  /**
   * Creates a new bundled connection from the bundled driver
   * properties.
   * 
   * @return Database connection
   * @throws DatabaseConnectorException
   *         On an exception
   */
  Connection createConnection()
    throws DatabaseConnectorException;

  /**
   * Gets the name of the datasource.
   * 
   * @return Datasource name.
   */
  String getDataSourceName();

  /**
   * Whether the name of the datasource has been defined.
   * 
   * @return Whether the name of the datasource has been defined.
   */
  boolean hasDataSourceName();

}
