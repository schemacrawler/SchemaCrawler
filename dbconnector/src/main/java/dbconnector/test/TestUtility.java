/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package dbconnector.test;


import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.Server;

import sf.util.CommandLineUtility;
import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Sets up a database schema for tests and examples.
 * 
 * @author sfatehi
 */
public class TestUtility
{

  private static final Logger LOGGER = Logger.getLogger(TestUtility.class
    .getName());

  private static final boolean DEBUG = false;

  private static final Class<Driver> driver;
  static
  {
    driver = loadJdbcDriver();
  }

  /**
   * Starts up a test database in server mode.
   * 
   * @param args
   *        Command line arguments
   * @throws Exception
   *         Exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    final TestUtility testUtility = new TestUtility();
    testUtility.createDatabase();
  }

  private static Class<Driver> loadJdbcDriver()
  {
    Class<Driver> driver = null;
    try
    {
      driver = (Class<Driver>) Class.forName("org.hsqldb.jdbcDriver");
    }
    catch (final ClassNotFoundException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    return driver;
  }

  protected PropertiesDataSource dataSource;

  protected PrintWriter out;

  /**
   * Load driver, and create database, schema and data
   * 
   * @throws PropertiesDataSourceException
   */
  public void createDatabase()
    throws PropertiesDataSourceException
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up database");
    // Attempt to delete the database files
    final String serverFileStem = "hsqldb.schemacrawler";
    deleteServerFiles(serverFileStem);
    // Start the server
    Server.main(new String[] {
        "-database.0",
        serverFileStem,
        "-dbname.0",
        "schemacrawler",
        "-silent",
        "false",
        "-trace",
        "true"
    });
    createDatabase("jdbc:hsqldb:hsql://localhost/schemacrawler");
  }

  /**
   * Load driver, and create database, schema and data
   * 
   * @throws PropertiesDataSourceException
   */
  public void createMemoryDatabase()
    throws PropertiesDataSourceException
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up in-memory database");
    createDatabase("jdbc:hsqldb:mem:schemacrawler");
  }

  /**
   * Gets the datasource.
   * 
   * @return Datasource
   */
  public PropertiesDataSource getDataSource()
  {
    return dataSource;
  }

  /**
   * Globally sets the application log level.
   */
  public void setApplicationLogLevel()
  {
    if (DEBUG)
    {
      CommandLineUtility.setApplicationLogLevel(Level.FINEST);
      out = new PrintWriter(System.out, true);
    }
    else
    {
      CommandLineUtility.setApplicationLogLevel(Level.OFF);
      out = new PrintWriter(new NullWriter(), true);
    }

  }

  /**
   * Suts down the database server.
   */
  public synchronized void shutdownDatabase()
  {
    try
    {
      if (dataSource != null)
      {
        final Connection connection = dataSource.getConnection();
        if (connection != null)
        {
          final Statement st = connection.createStatement();
          st.execute("SHUTDOWN");
          connection.close();
        }
        dataSource = null;
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "", e);
    }
  }

  private void createDatabase(final String url)
    throws PropertiesDataSourceException
  {
    makeDataSource(url);
    dataSource.setLogWriter(out);
    setupSchema();
  }

  private void deleteServerFiles(final String stem)
  {
    try
    {
      final File[] files = new File(".")
        .listFiles(new HSQLDBServerFilesFilter(stem));
      for (final File file: files)
      {
        if (!file.isDirectory() && !file.isHidden())
        {
          file.delete();
        }
      }
    }
    catch (final RuntimeException e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
    }
  }

  private void makeDataSource(final String url)
    throws PropertiesDataSourceException
  {
    final String DATASOURCE_NAME = "schemacrawler";

    final Properties connectionProperties = new Properties();
    connectionProperties.setProperty(DATASOURCE_NAME + ".driver", driver
      .getName());
    connectionProperties.setProperty(DATASOURCE_NAME + ".url", url);
    connectionProperties.setProperty(DATASOURCE_NAME + ".user", "sa");
    connectionProperties.setProperty(DATASOURCE_NAME + ".password", "");

    dataSource = new PropertiesDataSource(connectionProperties, DATASOURCE_NAME);
  }

  private synchronized void setupSchema()
  {
    Connection connection = null;
    Statement statement = null;
    try
    {
      // Load schema script file
      final String script = new String(Utilities.readFully(TestUtility.class
        .getResourceAsStream("/schemacrawler.test.sql")));
      if (dataSource != null)
      {
        connection = dataSource.getConnection();
        statement = connection.createStatement();
        statement.execute(script);
        connection.close();
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "", e);
    }
    finally
    {
      if (statement != null)
      {
        try
        {
          statement.close();
        }
        catch (final SQLException e)
        {
          LOGGER.log(Level.WARNING, "", e);
        }
      }
      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (final SQLException e)
        {
          LOGGER.log(Level.WARNING, "", e);
        }
      }
    }
  }

}
