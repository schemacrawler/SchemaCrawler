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

}
