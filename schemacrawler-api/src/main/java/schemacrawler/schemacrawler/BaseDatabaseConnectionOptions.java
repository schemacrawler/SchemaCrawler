/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.schemacrawler;


import static sf.util.Utility.isBlank;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.StringFormat;
import sf.util.TemplatingUtility;

abstract class BaseDatabaseConnectionOptions
  implements ConnectionOptions
{

  private static final long serialVersionUID = -8141436553988174836L;

  private static final Logger LOGGER = Logger
    .getLogger(BaseDatabaseConnectionOptions.class.getName());

  private static final String URL = "url";
  private static final String USER = "user";
  private static final String PASSWORD = "password";

  protected static final Map<String, String> toMap(final String connectionUrl)
  {
    if (isBlank(connectionUrl))
    {
      throw new NullPointerException("No database connection URL provided");
    }

    final Map<String, String> connectionProperties = new HashMap<>();
    connectionProperties.put(URL, connectionUrl);
    return connectionProperties;
  }

  protected final Map<String, String> connectionProperties;
  private String user;
  private String password;

  protected BaseDatabaseConnectionOptions(final Map<String, String> properties)
    throws SchemaCrawlerException
  {
    if (properties == null || properties.isEmpty())
    {
      throw new IllegalArgumentException("No connection properties provided");
    }

    setUser(properties.get(USER));
    setPassword(properties.get(PASSWORD));

    connectionProperties = new HashMap<>(properties);
    TemplatingUtility.substituteVariables(connectionProperties);
  }

  @Override
  public final Connection getConnection()
    throws SQLException
  {
    return getConnection(user, password);
  }

  @Override
  public final Connection getConnection(final String user,
                                        final String password)
    throws SQLException
  {
    if (isBlank(user))
    {
      LOGGER.log(Level.WARNING, "Database user is not provided");
    }
    if (isBlank(password))
    {
      LOGGER.log(Level.WARNING, "Database password is not provided");
    }

    String connectionUrl;
    try
    {
      connectionUrl = getConnectionUrl();
      if (isBlank(connectionUrl))
      {
        throw new IllegalArgumentException("No database connection URL provided");
      }
    }
    catch (final Exception e)
    {
      throw new SQLException(String
        .format("Could not connect to database, for user \'%s\'", user), e);
    }

    final Properties jdbcConnectionProperties = createConnectionProperties(user,
                                                                           password);
    try
    {
      LOGGER
        .log(Level.INFO,
             new StringFormat("Making connection to %s%nfor user \'%s\', with properties %s",
                              connectionUrl,
                              user,
                              safeProperties(jdbcConnectionProperties)));
      final Connection connection = DriverManager
        .getConnection(connectionUrl, jdbcConnectionProperties);

      LOGGER
        .log(Level.INFO,
             new StringFormat("Opened database connection, %s", connection));
      logConnection(connection);

      return connection;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException(String
        .format("Could not connect to %s, for user \'%s\', with properties %s",
                connectionUrl,
                user,
                safeProperties(jdbcConnectionProperties)), e);
    }
  }

  @Override
  public String getConnectionUrl()
  {
    final String connectionUrl = connectionProperties.get(URL);

    // Check that all required parameters have been substituted
    final Set<String> unmatchedVariables = TemplatingUtility
      .extractTemplateVariables(connectionUrl);
    if (!unmatchedVariables.isEmpty())
    {
      throw new IllegalArgumentException(String.format(
                                                       "Insufficient parameters for database connection URL: missing %s",
                                                       unmatchedVariables));
    }

    return connectionUrl;
  }

  @Override
  public final Driver getJdbcDriver()
    throws SQLException
  {
    try
    {
      return DriverManager.getDriver(getConnectionUrl());
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not find a suitable JDBC driver for database connection URL, "
                                          + getConnectionUrl(), e);
    }
  }

  @Override
  public int getLoginTimeout()
    throws SQLException
  {
    return 0;
  }

  @Override
  public PrintWriter getLogWriter()
    throws SQLException
  {
    return null;
  }

  @Override
  public Logger getParentLogger()
    throws SQLFeatureNotSupportedException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public final String getUser()
  {
    return user;
  }

  @Override
  public boolean isWrapperFor(final Class<?> iface)
    throws SQLException
  {
    return false;
  }

  @Override
  public void setLoginTimeout(final int seconds)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setLogWriter(final PrintWriter out)
    throws SQLException
  {
    if (out != null)
    {
      throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
    }
  }

  @Override
  public final void setPassword(final String password)
  {
    this.password = password;
  }

  @Override
  public final void setUser(final String user)
  {
    this.user = user;
  }

  @Override
  public final String toString()
  {
    String jdbcDriverClass = "<unknown>";
    try
    {
      final Driver jdbcDriver = getJdbcDriver();
      jdbcDriverClass = jdbcDriver.getClass().getName();
    }
    catch (final SQLException e)
    {
      jdbcDriverClass = "<unknown>";
    }

    final StringBuilder builder = new StringBuilder(1024);
    builder.append("driver=").append(jdbcDriverClass)
      .append(System.lineSeparator());
    builder.append("url=").append(getConnectionUrl())
      .append(System.lineSeparator());
    builder.append("user=").append(getUser()).append(System.lineSeparator());
    return builder.toString();
  }

  @Override
  public <T> T unwrap(final Class<T> iface)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  private Properties createConnectionProperties(final String user,
                                                final String password)
    throws SQLException
  {
    final List<String> skipProperties = Arrays
      .asList("server",
              "host",
              "port",
              "database",
              "urlx",
              "user",
              "password",
              "url");
    final Driver jdbcDriver = getJdbcDriver();
    final DriverPropertyInfo[] propertyInfo = jdbcDriver
      .getPropertyInfo(getConnectionUrl(), new Properties());
    final Map<String, Boolean> jdbcDriverProperties = new HashMap<>();
    for (final DriverPropertyInfo driverPropertyInfo: propertyInfo)
    {
      final String jdbcPropertyName = driverPropertyInfo.name.toLowerCase();
      if (skipProperties.contains(jdbcPropertyName))
      {
        continue;
      }
      jdbcDriverProperties.put(jdbcPropertyName, driverPropertyInfo.required);
    }

    final Properties jdbcConnectionProperties = new Properties();
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
      for (final String connectionProperty: connectionProperties.keySet())
      {
        final String value = connectionProperties.get(connectionProperty);
        if (jdbcDriverProperties.containsKey(connectionProperty.toLowerCase())
            && value != null)
        {
          jdbcConnectionProperties.put(connectionProperty, value);
        }
      }

      final Properties urlxConnectionProperties = parseConnectionProperties(connectionProperties
        .get("urlx"));
      jdbcConnectionProperties.putAll(urlxConnectionProperties);
    }

    return jdbcConnectionProperties;
  }

  private void logConnection(final Connection connection)
  {
    if (connection == null || !LOGGER.isLoggable(Level.INFO))
    {
      return;
    }
    try
    {
      final DatabaseMetaData dbMetaData = connection.getMetaData();
      LOGGER.log(Level.INFO,
                 new StringFormat("Connected to %n%s %s %nusing JDBC driver %n%s %s",
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

  private Properties parseConnectionProperties(final String connectionPropertiesString)
  {
    final Properties urlxProperties = new Properties();
    if (!isBlank(connectionPropertiesString))
    {
      for (final String property: connectionPropertiesString.split(";"))
      {
        if (!isBlank(property))
        {
          final String[] propertyValues = property.split("=");
          if (propertyValues.length >= 2)
          {
            final String key = propertyValues[0];
            final String value = propertyValues[1];
            if (key != null && value != null)
            {
              // Properties is based on Hashtable, which cannot take
              // null keys or values
              urlxProperties.put(key, value);
            }
          }
        }
      }
    }

    return urlxProperties;
  }

  private Properties safeProperties(final Properties properties)
  {
    final Properties logProperties = new Properties(properties);
    if (properties.contains("password"))
    {
      logProperties.put("password", "*****");
    }
    return logProperties;
  }

}
