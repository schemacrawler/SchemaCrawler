package schemacrawler.main.dbconnector;


import javax.sql.DataSource;

import schemacrawler.utility.TestDatabaseUtility;

public class TestUtilityDatabaseConnector
  implements DatabaseConnector
{

  private final TestDatabaseUtility testUtility;

  /**
   * Adapts a test utility instance to a DatabaseConnector. The
   * assumption is that the underlying test utility data-source has
   * already been created.
   * 
   * @param testUtility
   *        Test utility
   */
  public TestUtilityDatabaseConnector(final TestDatabaseUtility testUtility)
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
