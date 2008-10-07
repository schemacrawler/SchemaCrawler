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

package schemacrawler.utility.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.hsqldb.Server;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;
import schemacrawler.utility.datasource.PropertiesDataSource;

/**
 * Sets up a database schema for tests and examples.
 * 
 * @author sfatehi
 */
@SuppressWarnings("unchecked")
public class TestUtility
{

  private static final Level DEBUG_loglevel = Level.OFF;

  /**
   * System specific line separator character.
   */
  private static final String NEWLINE = System.getProperty("line.separator");

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

  public static void setApplicationLogLevel()
  {
    setApplicationLogLevel(DEBUG_loglevel);
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
      final String script = readFully(new InputStreamReader(TestUtility.class
        .getResourceAsStream("/schemacrawler.test.sql")));
      final String otherScript = readFully(new InputStreamReader(TestUtility.class
        .getResourceAsStream("/schemacrawler.test.other.sql")));
      if (dataSource != null)
      {
        connection = dataSource.getConnection();
        statement = connection.createStatement();

        statement.execute(script);
        connection.commit();

        statement.execute(otherScript);
        connection.commit();

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

  /**
   * Reads the stream fully, and returns a byte array of data.
   * 
   * @param stream
   *        Stream to read.
   * @return Byte array
   */
  private static String readFully(final Reader reader)
  {
    if (reader == null)
    {
      return "";
    }

    final StringBuffer buffer = new StringBuffer();
    try
    {
      final BufferedReader in = new BufferedReader(reader);
      String line;
      while ((line = in.readLine()) != null)
      {
        buffer.append(line).append(NEWLINE);
      }
      in.close();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error reading input stream", e);
    }

    return buffer.toString();
  }

  private static void setApplicationLogLevel(final Level logLevel)
  {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames(); loggerNames
      .hasMoreElements();)
    {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      logger.setLevel(null);
      final Handler[] handlers = logger.getHandlers();
      for (final Handler handler: handlers)
      {
        handler.setLevel(logLevel);
      }
    }

    final Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(logLevel);
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
    if (DEBUG)
    {
      out = new PrintWriter(System.out, true);
    }
    else
    {
      out = new PrintWriter(new NullWriter(), true);
    }
    createDatabase("jdbc:hsqldb:mem:schemacrawler");
  }

  public Catalog getCatalog(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    try
    {
      Catalog catalog = SchemaCrawlerUtility.getCatalog(getDataSource()
        .getConnection(), schemaCrawlerOptions);
      return catalog;
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.SEVERE, "Could not obtain a connection", e);
      return null;
    }
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

  public Schema getSchema(final SchemaCrawlerOptions schemaCrawlerOptions,
                          String schemaName)
  {
    Catalog catalog = getCatalog(schemaCrawlerOptions);

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

    dataSource = new PropertiesDataSource(connectionProperties, dataSourceName);
  }

}
