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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.Server;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import sf.util.Utility;

/**
 * Sets up a database schema for tests and examples.
 * 
 * @author sfatehi
 */
public class TestDatabase
{

  private static final Level DEBUG_LOG_LEVEL = Level.OFF;

  private static final Logger LOGGER = Logger.getLogger(TestDatabase.class
    .getName());

  public static void initializeApplicationLogging()
  {
    Utility.setApplicationLogLevel(DEBUG_LOG_LEVEL);
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

  private DatabaseConnectionOptions connectionOptions;

  /**
   * Create database in memory.
   * 
   * @throws SchemaCrawlerException
   */
  public void createMemoryDatabase()
    throws SchemaCrawlerException
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up in-memory database");
    createDatabase("jdbc:hsqldb:mem:schemacrawler");
  }

  /**
   * Gets the connection.
   * 
   * @return Connection
   * @throws SQLException
   */
  public Connection getConnection()
    throws SchemaCrawlerException
  {
    return connectionOptions.createConnection();
  }

  public Database getDatabase(final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException, SQLException
  {
    final Database database = SchemaCrawlerUtility
      .getDatabase(getConnection(), schemaCrawlerOptions);
    return database;
  }

  public DatabaseConnectionOptions getDatabaseConnectionOptions()
  {
    return connectionOptions;
  }

  public Schema getSchema(final SchemaCrawlerOptions schemaCrawlerOptions,
                          final String schemaName)
    throws SchemaCrawlerException, SQLException
  {
    final Database database = getDatabase(schemaCrawlerOptions);
    final Schema schema = database.getSchema(schemaName);
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
      if (connectionOptions != null)
      {
        connection = connectionOptions.createConnection();
        if (connection != null)
        {
          statement = connection.createStatement();
          statement.execute("SHUTDOWN");
          connection.close();
        }
        connectionOptions = null;
      }
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, "", e);
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
   * 
   * @throws SchemaCrawlerException
   */
  private void createDatabase()
    throws SchemaCrawlerException
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
    throws SchemaCrawlerException
  {
    makeDataSource(url);
    setupSchema(connectionOptions);
  }

  /**
   * Delete files from the previous run of the database server.
   */
  private void deleteServerFiles(final String stem)
  {
    final FilenameFilter serverFilesFilter = new FilenameFilter()
    {
      public boolean accept(final File dir, final String name)
      {
        return Arrays.asList(new String[] {
            stem + ".lck", stem + ".log", stem + ".properties",
        }).contains(name);
      }
    };

    final File[] files = new File(".").listFiles(serverFilesFilter);
    for (final File file: files)
    {
      if (!file.isDirectory() && !file.isHidden())
      {
        final boolean delete = file.delete();
        if (!delete)
        {
          LOGGER.log(Level.FINE, "Could not delete " + file.getAbsolutePath());
        }
      }
    }
  }

  private void makeDataSource(final String url)
    throws SchemaCrawlerException
  {
    connectionOptions = new DatabaseConnectionOptions("org.hsqldb.jdbcDriver",
                                                      url);
    connectionOptions.setUser("sa");
    connectionOptions.setPassword("");
  }

  /**
   * Setup the schema.
   * 
   * @param dataSource
   *        Datasource
   */
  private void setupSchema(final DatabaseConnectionOptions dataSource)
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
        connection = dataSource.createConnection();
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
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, "", e);
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
