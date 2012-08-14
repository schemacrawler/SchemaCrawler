/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

package schemacrawler.test.utility;


import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.server.Server;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;

/**
 * Sets up a database schema for tests and examples.
 * 
 * @author sfatehi
 */
public class TestDatabase
{

  private static final Logger LOGGER = Logger.getLogger(TestDatabase.class
    .getName());

  private static final String serverFileStem = "hsqldb.schemacrawler";

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
    final TestDatabase testDatabase = new TestDatabase("jdbc:hsqldb:hsql://localhost/schemacrawler");
    testDatabase.trace = true;
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run()
      {
        testDatabase.stop();
      }
    });
    testDatabase.start();
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

  private final String url;
  private boolean trace;

  public TestDatabase(final String url)
  {
    this.url = url;
  }

  /**
   * Load driver, and create database, schema and data.
   * 
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void start()
    throws Exception
  {
    LOGGER.log(Level.FINE, toString() + " - Setting up database");
    // Attempt to delete the database files
    deleteServerFiles();
    // Start the server
    final Server server = new Server();
    server.setDatabaseName(0, "schemacrawler");
    server.setDatabasePath(0, serverFileStem);
    server.setSilent(!trace);
    server.setTrace(trace);
    server.setLogWriter(null);
    server.setErrWriter(null);
    server.start();

    createDatabase();
  }

  /**
   * Shuts down the database server.
   */
  public void stop()
  {
    Connection connection = null;
    Statement statement = null;
    try
    {
      connection = getConnection();
      statement = connection.createStatement();
      statement.execute("SHUTDOWN");
      connection.close();
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

  private void createDatabase()
    throws SchemaCrawlerException
  {
    Connection connection = null;
    Statement statement = null;
    try
    {
      connection = getConnection();
      connection.setAutoCommit(true);
      statement = connection.createStatement();
      for (final String schema: new String[] {
          "books", "publisher sales", "for_lint",
      })
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

  private Connection getConnection()
    throws SQLException
  {
    return DriverManager.getConnection(url, "SA", "");
  }

}
