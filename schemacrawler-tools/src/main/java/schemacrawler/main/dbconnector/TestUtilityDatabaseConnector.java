package schemacrawler.main.dbconnector;


import javax.sql.DataSource;

import schemacrawler.utility.TestDatabase;

public class TestUtilityDatabaseConnector
  implements DatabaseConnector
{

  private final TestDatabase testUtility;

  /**
   * Adapts a test utility instance to a DatabaseConnector. The
   * assumption is that the underlying test utility data-source has
   * already been created.
   * 
   * @param testUtility
   *        Test utility
   */
  public TestUtilityDatabaseConnector(final TestDatabase testUtility)
  {
    this.testUtility = testUtility;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#createDataSource()
   */
  public DataSource createDataSource()
    throws DatabaseConnectorException
  {
    return testUtility.getDataSource();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#getDataSourceName()
   */
  public String getDataSourceName()
  {
    return "TestUtilityDataSource";
  }

  public boolean hasDataSourceName()
  {
    return true;
  }

}
