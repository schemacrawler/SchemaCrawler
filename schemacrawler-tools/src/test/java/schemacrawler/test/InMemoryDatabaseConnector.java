package schemacrawler.test;


import java.sql.Connection;
import java.sql.SQLException;

import schemacrawler.main.dbconnector.DatabaseConnector;
import schemacrawler.main.dbconnector.DatabaseConnectorException;
import schemacrawler.utility.TestDatabase;

public class InMemoryDatabaseConnector
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
  public InMemoryDatabaseConnector(final TestDatabase testDatabase)
  {
    this.testDatabase = testDatabase;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#createDataSource()
   */
  public Connection createConnection()
    throws DatabaseConnectorException
  {
    try
    {
      return testDatabase.getConnection();
    }
    catch (final SQLException e)
    {
      throw new DatabaseConnectorException("Could not create a connection");
    }
  }

}
