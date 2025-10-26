/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
      final String database = "schemacrawler%d".formatted(port);
      final boolean trace = false;
      final TestDatabase testDatabase = new TestDatabase(trace, host, port, database);
      testDatabase.start();
      return testDatabase;
    } catch (final Exception e) {
      throw new RuntimeException("Could not initialize test database", e);
    }
  }

  public static TestDatabase initializeStandard() {
    try {
      final String host = SC_DEFAULT_ADDRESS;
      final int port = SC_DEFAULT_HSQL_SERVER_PORT;
      final String database = "schemacrawler";
      final boolean trace = true;
      final TestDatabase testDatabase = new TestDatabase(trace, host, port, database);
      testDatabase.start();
      return testDatabase;
    } catch (final Exception e) {
      throw new RuntimeException("Could not initialize test database on default port", e);
    }
  }

  /**
   * Starts up a test database in server mode.
   *
   * @param args Command-line arguments
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    TestDatabase.initializeStandard();
  }

  private static int getFreePort() {
    final int defaultPort = 9001;
    try (final ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      final int port = socket.getLocalPort();
      if (port <= 0) {
        return defaultPort;
      }
      return port;
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
    LOGGER.log(Level.FINE, "%s - Setting up database".formatted(toString()));
    startServer();
    createTestDatabase();
  }

  /** Shut down the database server. */
  public void stop() {
    if (trace) {
      System.out.printf(
          "Stopping HyperSQL server for database %s:%d/%s%n", getHost(), getPort(), getDatabase());
    }
    stopServer();
  }

  private void createTestDatabase() throws SQLException {
    try (final Connection connection = getConnection(); ) {
      final TestSchemaCreator schemaCreator =
          new TestSchemaCreator(connection, "/hsqldb.scripts.txt", false);
      schemaCreator.run();
    }
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
        createTempDirectory("%s.%s".formatted(HSQLDB_SCHEMACRAWLER, database));

    // Start the server
    final Server server = new Server();
    server.setSilent(!trace);
    server.setTrace(trace);
    server.setLogWriter(logWriter);
    server.setErrWriter(errWriter);
    server.setAddress(host);
    server.setPort(port);
    server.setDatabaseName(0, database);
    server.setDatabasePath(0, "file:%s".formatted(tempDirectory));

    if (trace) {
      System.out.printf(
          "Starting HyperSQL server for database %s:%d/%s at %s%n",
          server.getAddress(),
          server.getPort(),
          server.getDatabaseName(0, true),
          server.getDatabasePath(0, true));
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
