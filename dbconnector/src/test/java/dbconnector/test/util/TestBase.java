package dbconnector.test.util;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.hsqldb.Server;

import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Based on org.hsqldb.test.TestBase by boucherb@users
 */
public abstract class TestBase
  extends TestCase
{

  private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";

  private static final Logger LOGGER = Logger.getLogger(TestBase.class
    .getName());

  private static final String HSQLDB_FILE_URL = "jdbc:hsqldb:file:_distrib/dbserver/schemacrawler;shutdown=true";
  private static final String HSQLDB_SERVER_URL = "jdbc:hsqldb:hsql://localhost/schemacrawler";

  private static final boolean IS_SERVER = true;
  private static final boolean START_SERVER = false;
  private static final boolean DEBUG = false;

  protected String serverProps;
  protected String url;
  protected String user = "sa";
  protected String password = "";
  protected Server server;

  protected PropertiesDataSource dataSource;
  protected PrintWriter out;

  public TestBase(String name)
  {
    super(name);
  }

  protected void setUp()
    throws PropertiesDataSourceException, ClassNotFoundException
  {

    LOGGER.log(Level.FINE, toString() + " - Setting up unit tests");
    if (DEBUG)
    {
      Utilities.setApplicationLogLevel(Level.FINEST);
      out = new PrintWriter(System.out, true);
    }
    else
    {
      Utilities.setApplicationLogLevel(Level.OFF);
      out = NullWriter.getNullPrintWriter();
    }

    if (IS_SERVER)
    {
      url = HSQLDB_SERVER_URL;
      if (START_SERVER)
      {
        server = new Server();
        server.setDatabaseName(0, "schemacrawler");
        server.setDatabasePath(0, "_distrib/dbserver/schemacrawler");
        server.start();
      }
    }
    else
    {
      url = HSQLDB_FILE_URL;
    }

    Class.forName(HSQLDB_JDBC_DRIVER);
    makeDataSource(url);
    dataSource.setLogWriter(out);

  }

  protected void tearDown()
  {
    LOGGER.log(Level.FINE, toString() + " - Tearing down unit tests");
    closeDataSource();
    if (IS_SERVER && START_SERVER)
    {
      server.shutdown();
    }
  }

  private synchronized void makeDataSource(String url)
    throws PropertiesDataSourceException
  {
    final String DATASOURCE_NAME = "schemacrawler";

    Properties connectionProperties = new Properties();
    connectionProperties.setProperty(DATASOURCE_NAME + ".driver",
                                     HSQLDB_JDBC_DRIVER);
    connectionProperties.setProperty(DATASOURCE_NAME + ".url", url);
    connectionProperties.setProperty(DATASOURCE_NAME + ".user", "sa");
    connectionProperties.setProperty(DATASOURCE_NAME + ".password", "");

    dataSource = new PropertiesDataSource(connectionProperties, DATASOURCE_NAME);
  }

  public synchronized PropertiesDataSource getDataSource()
    throws PropertiesDataSourceException
  {
    return dataSource;
  }

  synchronized void closeDataSource()
  {
    try
    {
      if (dataSource != null)
      {
        Connection connection = dataSource.getConnection();
        if (connection != null)
        {
          connection.close();
        }
        dataSource = null;
      }
    }
    catch (SQLException e)
    {
      LOGGER.log(Level.WARNING, "", e);
    }
  }

}
