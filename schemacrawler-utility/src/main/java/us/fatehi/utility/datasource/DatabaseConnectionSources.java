/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.datasource;

import static us.fatehi.utility.PropertiesUtility.getSystemConfigurationProperty;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DatabaseConnectionSources {

  private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionSources.class.getName());

  public static DatabaseConnectionSource fromConnection(final Connection connection) {
    return new ConnectionDatabaseConnectionSource(connection);
  }

  public static DatabaseConnectionSource fromDataSource(final DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials,
      final Consumer<Connection> connectionInitializer) {

    final boolean isSingleThreaded =
        Boolean.valueOf(
            getSystemConfigurationProperty("SC_SINGLE_THREADED", Boolean.FALSE.toString()));
    if (isSingleThreaded) {
      LOGGER.log(Level.CONFIG, "Loading database schema in the main thread");
      return new SingleDatabaseConnectionSource(
          connectionUrl, connectionProperties, userCredentials, connectionInitializer);
    }
    LOGGER.log(Level.CONFIG, "Loading database schema using multiple threads");
    return new SimpleDatabaseConnectionSource(
        connectionUrl, connectionProperties, userCredentials, connectionInitializer);
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl, final UserCredentials userCredentials) {
    return newDatabaseConnectionSource(connectionUrl, null, userCredentials, connection -> {});
  }

  private DatabaseConnectionSources() {
    // Prevent instantiation
  }
}
