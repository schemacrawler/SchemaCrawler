/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import static java.nio.file.Files.delete;
import static java.nio.file.Files.walkFileTree;
import static sf.util.DatabaseUtility.executeScriptFromResource;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.server.Server;

import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Sets up a database schema for tests and examples.
 *
 * @author sfatehi
 */
public class TestDatabase
{

  public static void initialize()
  {
    if (!initialized)
    {
      run(false);
      initialized = true;
    }
  }

  /**
   * Starts up a test database in server mode.
   *
   * @param args
   *        Command-line arguments
   * @throws Exception
   *         Exception
   */
  public static void main(final String[] args)
  {
    run(true);
  }

  /**
   * Delete files from the previous run of the database server.
   *
   * @param stem
   *        File stem
   * @throws IOException
   */
  private static void deleteServerFiles()
    throws IOException
  {
    final Path start = Paths.get(".").normalize().toAbsolutePath();
    walkFileTree(start, new SimpleFileVisitor<Path>()
    {
      @Override
      public FileVisitResult visitFile(final Path file,
                                       final BasicFileAttributes attrs)
        throws IOException
      {
        for (final String filename: new String[] {
            serverFileStem + ".lck",
            serverFileStem + ".log",
            serverFileStem + ".lobs",
            serverFileStem + ".script",
            serverFileStem + ".properties"
        })
        {
          if (!attrs.isDirectory() && file.endsWith(filename))
          {
            delete(file);
          }
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(final Path file,
                                             final IOException exc)
        throws IOException
      {
        return FileVisitResult.CONTINUE;
      }

    });
  }

  private static void run(final boolean trace)
  {
    try
    {
      final TestDatabase testDatabase = new TestDatabase(CONNECTION_STRING,
                                                         trace);
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
    catch (final Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(TestDatabase.class
    .getName());

  public static final String CONNECTION_STRING = "jdbc:hsqldb:hsql://localhost/schemacrawler";
  private static final String serverFileStem = "hsqldb.schemacrawler";

  public static boolean initialized;

  private final String url;
  private final boolean trace;

  public TestDatabase(final String url, final boolean trace)
  {
    this.url = url;
    this.trace = trace;
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

    // Set up writers
    final PrintWriter logWriter;
    final PrintWriter errWriter;
    if (trace)
    {
      logWriter = new PrintWriter(System.out);
      errWriter = new PrintWriter(System.err);
    }
    else
    {
      logWriter = null;
      errWriter = null;
    }

    // Start the server
    final Server server = new Server();
    server.setSilent(!trace);
    server.setTrace(trace);
    server.setLogWriter(logWriter);
    server.setErrWriter(errWriter);
    server.setDatabaseName(0, "schemacrawler");
    server.setDatabasePath(0, serverFileStem);
    server.start();

    createDatabase();
  }

  /**
   * Shuts down the database server.
   *
   * @throws IOException
   */
  public void stop()
  {
    try (final Connection connection = getConnection();
        final Statement statement = connection.createStatement();)
    {
      statement.execute("SHUTDOWN");
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    try
    {
      deleteServerFiles();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    LOGGER.log(Level.INFO, "SHUTDOWN database");
  }

  private void createDatabase()
    throws SchemaCrawlerException
  {
    try (final Connection connection = getConnection();)
    {
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
          executeScriptFromResource(scriptResource, connection);
        }
      }
    }
    catch (final SQLException e)
    {
      System.err.println(e.getMessage());
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

  private Connection getConnection()
    throws SQLException
  {
    final Connection connection = DriverManager.getConnection(url, "SA", "");
    connection.setAutoCommit(true);
    return connection;
  }

}
