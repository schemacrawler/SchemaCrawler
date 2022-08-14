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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.fatehi.utility.PropertiesUtility;

public class DatabaseConnectionSources {

  private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionSources.class.getName());

  @Deprecated
  public static DatabaseConnectionSource newDatabaseConnectionSource(final Connection connection)
      throws SQLException {
    return new SingleDatabaseConnectionSource("<no database connection URL>", connection);
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final UserCredentials userCredentials) {

    final String experimentalFlag =
        PropertiesUtility.getSystemConfigurationProperty(
            "SC_EXPERIMENTAL", Boolean.FALSE.toString());
    final Boolean isExperimental = Boolean.valueOf(experimentalFlag);
    if (isExperimental) {
      LOGGER.log(Level.CONFIG, "Loading database schema using multiple threads");
      return new SimpleDatabaseConnectionSource(
          connectionUrl, connectionProperties, userCredentials);
    } else {
      LOGGER.log(Level.CONFIG, "Loading database schema using a single main thread");
      return new SingleDatabaseConnectionSource(
          connectionUrl, connectionProperties, userCredentials);
    }
  }

  public static DatabaseConnectionSource newDatabaseConnectionSource(
      final String connectionUrl, final UserCredentials userCredentials) {
    return newDatabaseConnectionSource(connectionUrl, null, userCredentials);
  }

  private DatabaseConnectionSources() {
    // Prevent instantiation
  }
}
