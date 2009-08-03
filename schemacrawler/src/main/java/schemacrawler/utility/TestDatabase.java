/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.utility;


import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.hsqldb.Server;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.datasource.PropertiesDataSource;

/**
 * Sets up a database schema for tests and examples.
 * 
 * @author sfatehi
 */
@SuppressWarnings("unchecked")
public class TestDatabase
{

  private static final Level DEBUG_loglevel = Level.OFF;

  private static final Logger LOGGER = Logger.getLogger(TestDatabase.class
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

  public static void disableApplicationLogging()
  {
    Utility.setApplicationLogLevel(DEBUG_loglevel);
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
    final TestDatabase testUtility = new TestDatabase();
    testUtility.createDatabase();
  }

  private DataSource dataSource;
  private PrintWriter out;

  /**
   * Create database in memory.
   */
  public void createMemoryDatabase()
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up in-memory database");
    if (DEBUG)
    {
      out = new PrintWriter(System.out, true);
    }
    else
    {
      out = null;
    }
    createDatabase("jdbc:hsqldb:mem:schemacrawler");
  }

  public Catalog getCatalog(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    final Database database = getDatabase(schemaCrawlerOptions);

    final Catalog catalog = database.getCatalogs()[0];
    return catalog;
  }

  public Database getDatabase(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    try
    {
      final Database database = SchemaCrawlerUtility
        .getDatabase(getConnection(), schemaCrawlerOptions);
      return database;
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.SEVERE, "Could not obtain a connection", e);
      return null;
    }
  }

  /**
   * Gets the connection.
   * 
   * @return Connection
   * @throws SQLException
   */
  public Connection getConnection()
    throws SQLException
  {
    return dataSource.getConnection();
  }

  public Schema getSchema(final SchemaCrawlerOptions schemaCrawlerOptions,
                          final String schemaName)
  {
    final Catalog catalog = getCatalog(schemaCrawlerOptions);

    final Schema schema = catalog.getSchema(schemaName);
    return schema;
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

  /**
   * Load driver, and create database, schema and data.
   */
  private void createDatabase()
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

  /**
   * Delete files from the previous run of the database server.
   */
  private void deleteServerFiles(final String stem)
  {
    final File[] files = new File(".").listFiles(new FilenameFilter()
    {
      public boolean accept(final File dir, final String name)
      {
        return Arrays.asList(new String[] {
            stem + ".lck", stem + ".log", stem + ".properties",
        }).contains(name);
      }
    });
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

    dataSource = new PropertiesDataSource(connectionProperties, dataSourceName);
  }

  /**
   * Setup the schema.
   * 
   * @param dataSource
   *        Datasource
   */
  private void setupSchema(final DataSource dataSource)
  {
    Connection connection = null;
    Statement statement = null;
    try
    {
      final String[] scriptResources = new String[] {
          "/schemacrawler.test.sql",
          "/schemacrawler.test.extra.sql",
          "/schemacrawler.test.other.sql"
      };
      if (dataSource != null)
      {
        connection = dataSource.getConnection();
        statement = connection.createStatement();
        for (final String scriptResource: scriptResources)
        {
          final String script = Utility.readFully(TestDatabase.class
            .getResourceAsStream(scriptResource));
          statement.execute(script);
          connection.commit();
        }
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
