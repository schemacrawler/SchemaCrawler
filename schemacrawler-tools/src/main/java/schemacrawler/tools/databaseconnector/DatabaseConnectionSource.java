/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.databaseconnector;


import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.SchemaCrawlerLogger;
import us.fatehi.utility.string.StringFormat;

public final class DatabaseConnectionSource
  implements Supplier<Connection>
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(DatabaseConnectionSource.class.getName());

  private static void logConnection(final Connection connection)
  {
    if (connection == null || !LOGGER.isLoggable(Level.INFO))
    {
      return;
    }
    try
    {
      final DatabaseMetaData dbMetaData = connection.getMetaData();
      LOGGER.log(Level.INFO,
                 new StringFormat(
                   "Connected to %n%s %s %nusing JDBC driver %n%s %s",
                   dbMetaData.getDatabaseProductName(),
                   dbMetaData.getDatabaseProductVersion(),
                   dbMetaData.getDriverName(),
                   dbMetaData.getDriverVersion()));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not log connection information", e);
    }
  }

  private static Properties safeProperties(final Properties properties)
  {
    final Properties logProperties = new Properties(properties);
    if (properties.contains("password"))
    {
      logProperties.put("password", "*****");
    }
    return logProperties;
  }

  private final Map<String, String> connectionProperties;
  private final String connectionUrl;
  private UserCredentials userCredentials;

  public DatabaseConnectionSource(final String connectionUrl)
  {
    this(connectionUrl, null);
  }

  public DatabaseConnectionSource(final String connectionUrl,
                                  final Map<String, String> connectionProperties)
  {
    if (isBlank(connectionUrl))
    {
      throw new IllegalArgumentException("No database connection URL provided");
    }
    this.connectionUrl = connectionUrl;

    this.connectionProperties = connectionProperties;

    // Ensure that user credentials are not null
    userCredentials = new SingleUseUserCredentials();
  }

  public UserCredentials getUserCredentials()
  {
    return userCredentials;
  }

  public void setUserCredentials(final UserCredentials userCredentials)
  {
    this.userCredentials =
      requireNonNull(userCredentials, "No user credentials provided");
  }

  @Override
  public final Connection get()
  {
    final String user = userCredentials.getUser();
    final String password = userCredentials.getPassword();
    return getConnection(user, password);
  }

  public final Driver getJdbcDriver()
    throws SQLException
  {
    return getJdbcDriver(connectionUrl);
  }

  @Override
  public final String toString()
  {
    String jdbcDriverClass = "<unknown>";
    try
    {
      final Driver jdbcDriver = getJdbcDriver();
      jdbcDriverClass = jdbcDriver
        .getClass()
        .getName();
    }
    catch (final SQLException e)
    {
      jdbcDriverClass = "<unknown>";
    }

    final StringBuilder builder = new StringBuilder(1024);
    builder
      .append("driver=")
      .append(jdbcDriverClass)
      .append(System.lineSeparator());
    builder
      .append("url=")
      .append(connectionUrl)
      .append(System.lineSeparator());
    return builder.toString();
  }

  public String getConnectionUrl()
  {
    return connectionUrl;
  }

  private Connection getConnection(final String user, final String password)
  {
    if (isBlank(user))
    {
      LOGGER.log(Level.WARNING, "Database user is not provided");
    }
    if (isBlank(password))
    {
      LOGGER.log(Level.WARNING, "Database password is not provided");
    }

    final Properties jdbcConnectionProperties =
      createConnectionProperties(connectionUrl, user, password);
    try
    {
      LOGGER.log(Level.INFO,
                 new StringFormat(
                   "Making connection to %s%nfor user \'%s\', with properties %s",
                   connectionUrl,
                   user,
                   safeProperties(jdbcConnectionProperties)));
      // (Using java.sql.DriverManager.getConnection(String, Properties)
      // to make a connection is not the best idea,
      // since for some strange reason, it does not check if a Driver
      // will accept the connection URL, and some non-compliant drivers
      // (MySQL Connector/J) may raise an exception other than a
      // SQLException in this case.)
      final Driver driver = getJdbcDriver(connectionUrl);
      final Connection connection =
        driver.connect(connectionUrl, jdbcConnectionProperties);

      LOGGER.log(Level.INFO,
                 new StringFormat("Opened database connection <%s>",
                                  connection));
      logConnection(connection);

      // Clear password
      jdbcConnectionProperties.remove("password");

      return connection;
    }
    catch (final SQLException e)
    {
      final String username;
      if (user != null)
      {
        username = String.format("user \'%s\'", user);
      }
      else
      {
        username = "unspecified user";
      }
      throw new SchemaCrawlerRuntimeException(String.format(
        "Could not connect to %s, for %s, with properties %s",
        connectionUrl,
        username,
        safeProperties(jdbcConnectionProperties)), e);
    }
  }

  private Properties createConnectionProperties(final String connectionUrl,
                                                final String user,
                                                final String password)
  {
    final List<String> skipProperties = Arrays.asList("server",
                                                      "host",
                                                      "port",
                                                      "database",
                                                      "urlx",
                                                      "user",
                                                      "password",
                                                      "url");
    final Properties jdbcConnectionProperties;
    try
    {
      final Driver jdbcDriver = getJdbcDriver(connectionUrl);
      final DriverPropertyInfo[] propertyInfo =
        jdbcDriver.getPropertyInfo(this.connectionUrl, new Properties());
      final Map<String, Boolean> jdbcDriverProperties = new HashMap<>();
      for (final DriverPropertyInfo driverPropertyInfo : propertyInfo)
      {
        final String jdbcPropertyName = driverPropertyInfo.name.toLowerCase();
        if (skipProperties.contains(jdbcPropertyName))
        {
          continue;
        }
        jdbcDriverProperties.put(jdbcPropertyName, driverPropertyInfo.required);
      }

      jdbcConnectionProperties = new Properties();
      if (user != null)
      {
        jdbcConnectionProperties.put("user", user);
      }
      if (password != null)
      {
        jdbcConnectionProperties.put("password", password);
      }
      if (connectionProperties != null)
      {
        for (final Map.Entry<String, String> connectionProperty : connectionProperties.entrySet())
        {
          final String property = connectionProperty.getKey();
          final String value = connectionProperty.getValue();
          if (jdbcDriverProperties.containsKey(property.toLowerCase())
              && value != null)
          {
            jdbcConnectionProperties.put(property, value);
          }
        }
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Could not get connection properties",
        e);
    }

    return jdbcConnectionProperties;
  }

  private Driver getJdbcDriver(final String connectionUrl)
    throws SQLException
  {
    try
    {
      return DriverManager.getDriver(connectionUrl);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException(
        "Could not find a suitable JDBC driver for database connection URL, "
        + this.connectionUrl,
        e);
    }
  }

}
