/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Objects.requireNonNull;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.fatehi.utility.SQLRuntimeException;
import us.fatehi.utility.string.StringFormat;

abstract class AbstractDatabaseConnectionSource implements DatabaseConnectionSource {

  private static final Logger LOGGER =
      Logger.getLogger(AbstractDatabaseConnectionSource.class.getName());

  protected static Properties createConnectionProperties(
      final String connectionUrl,
      final Map<String, String> connectionProperties,
      final String user,
      final String password) {
    final List<String> skipProperties =
        Arrays.asList("server", "host", "port", "database", "urlx", "user", "password", "url");
    final Properties jdbcConnectionProperties;
    try {
      final Driver jdbcDriver = getJdbcDriver(connectionUrl);
      final DriverPropertyInfo[] propertyInfo =
          jdbcDriver.getPropertyInfo(connectionUrl, new Properties());
      final Map<String, Boolean> jdbcDriverProperties = new HashMap<>();
      for (final DriverPropertyInfo driverPropertyInfo : propertyInfo) {
        final String jdbcPropertyName = driverPropertyInfo.name.toLowerCase();
        if (skipProperties.contains(jdbcPropertyName)) {
          continue;
        }
        jdbcDriverProperties.put(jdbcPropertyName, driverPropertyInfo.required);
      }

      jdbcConnectionProperties = new Properties();
      if (user != null) {
        jdbcConnectionProperties.setProperty("user", user);
      }
      if (password != null) {
        jdbcConnectionProperties.setProperty("password", password);
      }
      if (connectionProperties != null) {
        for (final Map.Entry<String, String> connectionProperty : connectionProperties.entrySet()) {
          final String property = connectionProperty.getKey();
          final String value = connectionProperty.getValue();
          if (jdbcDriverProperties.containsKey(property.toLowerCase()) && value != null) {
            jdbcConnectionProperties.setProperty(property, value);
          }
        }
      }
    } catch (final SQLException e) {
      throw new SQLRuntimeException("Could not get connection properties", e);
    }

    return jdbcConnectionProperties;
  }

  protected static Connection getConnection(
      final String connectionUrl, final Properties jdbcConnectionProperties) {

    final String username;
    final String user = jdbcConnectionProperties.getProperty("user");
    if (user != null) {
      username = String.format("user '%s'", user);
    } else {
      username = "unspecified user";
    }

    try {
      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Making connection to %s%nfor user '%s', with properties %s",
              connectionUrl, username, safeProperties(jdbcConnectionProperties)));
      // (Using java.sql.DriverManager.getConnection(String, Properties)
      // to make a connection is not the best idea,
      // since for some strange reason, it does not check if a Driver
      // will accept the connection URL, and some non-compliant drivers
      // (MySQL Connector/J) may raise an exception other than a
      // SQLException in this case.)
      final Driver driver = getJdbcDriver(connectionUrl);
      final Connection connection = driver.connect(connectionUrl, jdbcConnectionProperties);
      LOGGER.log(Level.INFO, new StringFormat("Opened database connection <%s>", connection));
      return connection;
    } catch (final SQLException e) {
      throw new SQLRuntimeException(
          String.format(
              "Could not connect to <%s>, for <%s>, with properties <%s>",
              connectionUrl, username, safeProperties(jdbcConnectionProperties)),
          e);
    }
  }

  private static Driver getJdbcDriver(final String connectionUrl) throws SQLException {
    try {
      return DriverManager.getDriver(connectionUrl);
    } catch (final SQLException e) {
      throw new SQLException(
          String.format(
              "Could not find a suitable JDBC driver for database connection URL <%s>",
              connectionUrl),
          e);
    }
  }

  private static Properties safeProperties(final Properties properties) {
    final Properties logProperties = new Properties(properties);
    logProperties.remove("password");
    return logProperties;
  }

  protected Consumer<Connection> connectionInitializer;

  public AbstractDatabaseConnectionSource(final Consumer<Connection> connectionInitializer) {
    this.connectionInitializer =
        requireNonNull(connectionInitializer, "No connection initializer provided");
  }

  @Override
  public void setFirstConnectionInitializer(final Consumer<Connection> connectionInitializer) {
    if (connectionInitializer != null) {
      this.connectionInitializer = connectionInitializer.andThen(this.connectionInitializer);
    }
  }
}
