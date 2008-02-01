/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

import javax.sql.DataSource;

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
@SuppressWarnings("unchecked")
public class TestUtility
{

  private static final Logger LOGGER = Logger.getLogger(TestUtility.class
    .getName());

  private static final boolean DEBUG = false;

  private static final Class<Driver> JDBC_DRIVER_CLASS;
  static
  {
    try
    {
      JDBC_DRIVER_CLASS = (Class<Driver>) Class
        .forName("org.hsqldb.jdbcDriver");
    }
    catch (final ClassNotFoundException e)
    {
      throw new RuntimeException(e);
    }
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

  /**
   * Setup the schema.
   * 
   * @param dataSource
   *        Datasource
   */
  public static synchronized void setupSchema(final DataSource dataSource)
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

  protected DataSource dataSource;

  protected PrintWriter out;

  /**
   * Load driver, and create database, schema and data.
   */
  public void createDatabase()
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
   * Create database in memory.
   */
  public void createMemoryDatabase()
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up in-memory database");
    createDatabase("jdbc:hsqldb:mem:schemacrawler");
  }

  /**
   * Gets the datasource.
   * 
   * @return Datasource
   */
  public DataSource getDataSource()
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
   * Shuts down the database server.
   */
  public void shutdownDatabase()
  {
    Connection connection = null;
    Statement statement = null;
    try
    {
      if (dataSource != null)
      {
        connection = dataSource.getConnection();
        if (connection != null)
        {
          statement = connection.createStatement();
          statement.execute("SHUTDOWN");
          connection.close();
        }
        dataSource = null;
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

  private void createDatabase(final String url)
  {
    makeDataSource(url);
    try
    {
      dataSource.setLogWriter(out);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.FINE, "Could not set log writer", e);
    }
    setupSchema(dataSource);
  }

  private void deleteServerFiles(final String stem)
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

  private void makeDataSource(final String url)
  {
    final String dataSourceName = "schemacrawler";

    final Properties connectionProperties = new Properties();
    connectionProperties.setProperty(dataSourceName + ".driver",
                                     JDBC_DRIVER_CLASS.getName());
    connectionProperties.setProperty(dataSourceName + ".url", url);
    connectionProperties.setProperty(dataSourceName + ".user", "sa");
    connectionProperties.setProperty(dataSourceName + ".password", "");

    try
    {
      dataSource = new PropertiesDataSource(connectionProperties,
                                            dataSourceName);
    }
    catch (final PropertiesDataSourceException e)
    {
      throw new RuntimeException("Cannot create data source", e);
    }
  }

}
