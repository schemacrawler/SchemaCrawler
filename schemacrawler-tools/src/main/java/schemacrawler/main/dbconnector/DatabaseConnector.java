package schemacrawler.main.dbconnector;


import javax.sql.DataSource;

/**
 * Data-source parser.
 * 
 * @author sfatehi
 */
public interface DatabaseConnector
{

  /**
   * Creates a new bundled data source from the bundled driver
   * properties.
   * 
   * @return Data source
   * @throws DatabaseConnectorException
   *         On an exception
   */
  DataSource createDataSource()
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
