/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  private static final String serverFileStem = "hsqldb.schemacrawler";

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
    final TestDatabase testDb = new TestDatabase();
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run()
      {
        testDb.shutdownDatabase();
      }
    });
    testDb.startDatabase(false);
  }

  /**
   * Delete files from the previous run of the database server.
   * 
   * @param stem
   *        File stem
   */
  private static void deleteServerFiles()
  {
    final FilenameFilter serverFilesFilter = new FilenameFilter()
    {
      @Override
      public boolean accept(final File dir, final String name)
      {
        return Arrays.asList(serverFileStem + ".lck",
                             serverFileStem + ".log",
                             serverFileStem + ".lobs",
                             serverFileStem + ".script",
                             serverFileStem + ".properties").contains(name);
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

  /**
   * Setup the schema.
   * 
   * @param dataSource
   *        Datasource
   * @param schemas
   *        Schema names
   */
  private static void setupSchema(final DatabaseConnectionOptions dataSource,
                                  final String... schemas)
  {
    Connection connection = null;
    Statement statement = null;
    try
    {
      if (dataSource != null)
      {
        connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        statement = connection.createStatement();
        for (final String schema: schemas)
        {
          for (final String scriptType: new String[] {
              "pre_schema", "schema", "post_schema", "data",
          })
          {
            final String scriptResource = String
              .format("/testdatabase/%s.%s.sql", schema, scriptType)
              .toLowerCase(Locale.ENGLISH);
            final String sqlScript = Utility.readResourceFully(scriptResource);
            if (!Utility.isBlank(sqlScript))
            {
              for (final String sql: sqlScript.split(";"))
              {
                if (!Utility.isBlank(sql))
                {
                  statement.executeUpdate(sql);
                }
              }
            }
          }
        }
        connection.close();
      }
    }
    catch (final SQLException e)
    {
      System.err.println(e.getMessage());
      LOGGER.log(Level.WARNING, e.getMessage(), e);
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

  private final String[] schemas;

  private DatabaseConnectionOptions connectionOptions;

  public TestDatabase()
  {
    schemas = new String[] {
        "books", "publisher sales",
    };
  }

  public TestDatabase(final String... schemas)
  {
    this.schemas = schemas;
  }

  /**
   * Gets the connection.
   * 
   * @return Connection
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public Connection getConnection()
    throws SchemaCrawlerException
  {
    try
    {
      return connectionOptions.getConnection();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  public Database getDatabase(final SchemaCrawlerOptions schemaCrawlerOptions)
    throws SchemaCrawlerException
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
        connection = connectionOptions.getConnection();
        if (connection != null)
        {
          statement = connection.createStatement();
          statement.execute("SHUTDOWN");
          connection.close();
        }
        connectionOptions = null;
      }
      deleteServerFiles();
      LOGGER.log(Level.INFO, "SHUTDOWN database");
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
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
   *         On an exception
   */
  public void startDatabase(final boolean silent)
    throws SchemaCrawlerException
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up database");
    // Attempt to delete the database files
    deleteServerFiles();
    // Start the server
    org.hsqldb.server.Server.main(new String[] {
        "-database.0",
        serverFileStem,
        "-dbname.0",
        "schemacrawler",
        "-silent",
        Boolean.toString(silent),
        "-trace",
        Boolean.toString(!silent),
        "-no_system_exit",
        "true"
    });
    createDatabase("jdbc:hsqldb:hsql://localhost/schemacrawler");
  }

  /**
   * Create database in memory.
   * 
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void startMemoryDatabase()
    throws SchemaCrawlerException
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up in-memory database");
    createDatabase("jdbc:hsqldb:mem:schemacrawler");
  }

  private void createDatabase(final String url)
    throws SchemaCrawlerException
  {
    makeDataSource(url);
    setupSchema(connectionOptions, schemas);
  }

  private void makeDataSource(final String url)
    throws SchemaCrawlerException
  {
    connectionOptions = new DatabaseConnectionOptions("org.hsqldb.jdbc.JDBCDriver",
                                                      url);
    connectionOptions.setUser("sa");
    connectionOptions.setPassword("");
  }

}
