package dbconnector.dbconnector;


import sf.util.Config;

/**
 * Create implementations of
 * 
 * @author sfatehi
 */
public class DatabaseConnectorFactory
{

  /**
   * Creates a data-source parser that understands host, port, user
   * name,
   * 
   * @param args
   *        Command line arguments
   * @param config
   *        Base configuration
   * @return Data-source parser
   * @throws DatabaseConnectorException
   */
  public static DatabaseConnector createBundledDriverDataSourceParser(final String[] args,
                                                                      final Config config)
    throws DatabaseConnectorException
  {
    return new BundledDriverDatabaseConnector(args, config);
  }

  /**
   * Creates a data-source parser that understands host, port, user
   * name,
   * 
   * @param args
   *        Command line arguments
   * @param config
   *        Base configuration
   * @return Data-source parser
   * @throws DatabaseConnectorException
   */
  public static DatabaseConnector createPropertiesDriverDataSourceParser(final String[] args,
                                                                         final Config config)
    throws DatabaseConnectorException
  {
    return new PropertiesDataSourceDatabaseConnector(args, config);
  }

}
