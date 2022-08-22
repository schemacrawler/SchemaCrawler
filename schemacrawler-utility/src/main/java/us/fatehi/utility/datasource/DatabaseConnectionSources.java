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

  public static DatabaseConnectionSource fromDataSource(final DataSource dataSource) {
    return new DataSourceConnectionSource(dataSource);
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials,
      final Consumer<Connection> connectionInitializer) {

    final boolean isExperimental =
        Boolean.valueOf(
            getSystemConfigurationProperty("SC_EXPERIMENTAL", Boolean.FALSE.toString()));
    if (isExperimental) {
      LOGGER.log(Level.CONFIG, "Loading database schema using multiple threads");
      return new SimpleDatabaseConnectionSource(
          connectionUrl, connectionProperties, userCredentials, connectionInitializer);
    } else {
      LOGGER.log(Level.CONFIG, "Loading database schema using a single main thread");
      return new SingleDatabaseConnectionSource(
          connectionUrl, connectionProperties, userCredentials, connectionInitializer);
    }
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl, final UserCredentials userCredentials) {
    return newDatabaseConnectionSource(connectionUrl, null, userCredentials, connection -> {});
  }

  /** @deprecated */
  @Deprecated
  public static DatabaseConnectionSource wrappedDatabaseConnectionSource(
      final Connection connection, final Consumer<Connection> connectionInitializer) {
    final DatabaseConnectionSource dbConnectionSource =
        new WrappedDatabaseConnectionSource(connection, connectionInitializer);
    return dbConnectionSource;
  }

  private DatabaseConnectionSources() {
    // Prevent instantiation
  }
}
