/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.testdb;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.walkFileTree;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.server.Server;

/**
 * Sets up a database schema for tests and examples.
 *
 * @author sfatehi
 */
public class TestDatabase
{

  private static final Logger LOGGER = Logger
    .getLogger(TestDatabase.class.getName());

  public static final String CONNECTION_STRING = "jdbc:hsqldb:hsql://localhost/schemacrawler";

  private static final String serverFileStem = "hsqldb.schemacrawler";

  public static boolean initialized;

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
                                                   serverFileStem + ".properties" })
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
    LOGGER.log(Level.FINE,
               String.format("%s - Setting up database", toString()));
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
    if (!trace)
    {
      server.setLogWriter(new PrintWriter(new OutputStream()
      {
        @Override
        public void write(final int b)
          throws IOException
        {
        }
      }));
    }
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
  {
    try (final Connection connection = getConnection();)
    {
      for (final String schema: new String[] {
                                               "books",
                                               "publisher sales",
                                               "for_lint", })
      {
        for (final String scriptType: new String[] {
                                                     "pre_schema",
                                                     "schema",
                                                     "post_schema",
                                                     "data", })
        {
          final String scriptResource = String.format("/testdatabase/%s.%s.sql",
                                                      schema,
                                                      scriptType);
          final Reader reader = new InputStreamReader(TestDatabase.class
            .getResourceAsStream(scriptResource), UTF_8);

          final SqlScript sqlScript = new SqlScript(connection);
          sqlScript.run(reader);
        }
      }
    }
    catch (final SQLException | IOException e)
    {
      System.err.println(e.getMessage());
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      throw new RuntimeException(e);
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
