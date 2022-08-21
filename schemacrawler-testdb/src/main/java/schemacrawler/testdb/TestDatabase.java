/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.file.Files.createTempDirectory;
import static java.util.Objects.requireNonNull;
import static org.hsqldb.server.ServerConstants.SC_DEFAULT_ADDRESS;
import static org.hsqldb.server.ServerConstants.SC_DEFAULT_HSQL_SERVER_PORT;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.server.Server;

/** Sets up a database schema for tests and examples. */
public class TestDatabase {

  private static final Logger LOGGER = Logger.getLogger(TestDatabase.class.getName());

  private static final String CONNECTION_STRING = "jdbc:hsqldb:hsql://${host}:${port}/${database}";
  private static final String HSQLDB_SCHEMACRAWLER = "hsqldb.schemacrawler";

  public static TestDatabase initialize() {
    try {
      final String host = SC_DEFAULT_ADDRESS;
      final int port = getFreePort();
      final String database = String.format("schemacrawler%d", port);
      final boolean trace = false;
      final TestDatabase testDatabase = new TestDatabase(trace, host, port, database);
      testDatabase.start();
      return testDatabase;
    } catch (final Exception e) {
      throw new RuntimeException("Could not initialize test database", e);
      //      e.printStackTrace();
      //      System.exit(1);
      //      return null;
    }
  }

  public static TestDatabase initializeStandard() {
    try {
      final String host = SC_DEFAULT_ADDRESS;
      final int port = SC_DEFAULT_HSQL_SERVER_PORT;
      final String database = "schemacrawler";
      final boolean trace = false;
      final TestDatabase testDatabase = new TestDatabase(trace, host, port, database);
      testDatabase.start();
      return testDatabase;
    } catch (final Exception e) {
      throw new RuntimeException("Could not initialize test database", e);
      //      e.printStackTrace();
      //      System.exit(1);
      //      return null;
    }
  }

  /**
   * Starts up a test database in server mode.
   *
   * @param args Command-line arguments
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    final TestDatabase testDatabase =
        new TestDatabase(true, SC_DEFAULT_ADDRESS, SC_DEFAULT_HSQL_SERVER_PORT, "schemacrawler");
    testDatabase.start();
  }

  private static int getFreePort() {
    final int defaultPort = 9001;
    try (final ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      final int port = socket.getLocalPort();
      if (port <= 0) {
        return defaultPort;
      } else {
        return port;
      }
    } catch (final IOException e) {
      return defaultPort;
    }
  }

  private final boolean trace;
  private final String host;
  private final int port;
  private final String database;
  private final String url;

  private TestDatabase(
      final boolean trace, final String host, final int port, final String database) {
    this.trace = trace;
    this.host = requireNonNull(host);
    this.port = port;
    this.database = requireNonNull(database);

    url =
        CONNECTION_STRING
            .replace("${host}", host)
            .replace("${port}", String.valueOf(port))
            .replace("${database}", database);

    LOGGER.log(Level.CONFIG, url);
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, "sa", "");
  }

  public String getConnectionUrl() {
    return url;
  }

  public String getDatabase() {
    return database;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  /** Load driver, and create database, schema and data. */
  public void start() throws Exception {
    LOGGER.log(Level.FINE, String.format("%s - Setting up database", toString()));
    startServer();
    createTestDatabase();
  }

  /** Shut down the database server. */
  public void stop() {
    if (trace) {
      System.out.println(
          String.format(
              "Stopping HyperSQL server for database %s:%d/%s",
              getHost(), getPort(), getDatabase()));
    }
    stopServer();
  }

  private void createTestDatabase() throws SQLException {
    final Connection connection = getConnection();
    final TestSchemaCreator schemaCreator =
        new TestSchemaCreator(connection, "/hsqldb.scripts.txt");
    schemaCreator.run();
  }

  private void startServer() throws IOException {

    Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

    // Set up writers
    final PrintWriter logWriter;
    final PrintWriter errWriter;
    if (trace) {
      logWriter = new PrintWriter(System.out);
      errWriter = new PrintWriter(System.err);
    } else {
      logWriter = null;
      errWriter = null;
    }

    // Create temp directory
    final Path tempDirectory =
        createTempDirectory(String.format("%s.%s", HSQLDB_SCHEMACRAWLER, database));

    // Start the server
    final Server server = new Server();
    server.setSilent(!trace);
    server.setTrace(trace);
    server.setLogWriter(logWriter);
    server.setErrWriter(errWriter);
    server.setAddress(host);
    server.setPort(port);
    server.setDatabaseName(0, database);
    server.setDatabasePath(0, String.format("file:%s", tempDirectory));

    if (trace) {
      System.out.println(
          String.format(
              "Starting HyperSQL server for database %s:%d/%s at %s",
              server.getAddress(),
              server.getPort(),
              server.getDatabaseName(0, true),
              server.getDatabasePath(0, true)));
    }

    // Blocked server start
    server.start();
    server.checkRunning(true);
  }

  private void stopServer() {
    try (final Connection connection = getConnection();
        final Statement statement = connection.createStatement(); ) {
      statement.execute("SHUTDOWN");
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    LOGGER.log(Level.INFO, "SHUTDOWN database");
  }
}
