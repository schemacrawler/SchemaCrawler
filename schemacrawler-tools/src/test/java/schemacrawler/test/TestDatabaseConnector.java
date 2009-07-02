package schemacrawler.test;


import javax.sql.DataSource;

import schemacrawler.main.dbconnector.DatabaseConnector;
import schemacrawler.main.dbconnector.DatabaseConnectorException;
import schemacrawler.utility.TestDatabase;

public class TestDatabaseConnector
  implements DatabaseConnector
{

  private final TestDatabase testDatabase;

  /**
   * Adapts a test utility instance to a DatabaseConnector. The
   * assumption is that the underlying test utility data-source has
   * already been created.
   * 
   * @param testDatabase
   *        Test utility
   */
  public TestDatabaseConnector(final TestDatabase testDatabase)
  {
    this.testDatabase = testDatabase;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#createDataSource()
   */
  public DataSource createDataSource()
    throws DatabaseConnectorException
  {
    return testDatabase.getDataSource();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#getDataSourceName()
   */
  public String getDataSourceName()
  {
    return getClass().getSimpleName();
  }

  public boolean hasDataSourceName()
  {
    return true;
  }

}
