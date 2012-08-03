/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

package schemacrawler.schemacrawler;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utility;

abstract class BaseDatabaseConnectionOptions
  implements ConnectionOptions
{

  private static final long serialVersionUID = -8141436553988174836L;

  private static final Logger LOGGER = Logger
    .getLogger(BaseDatabaseConnectionOptions.class.getName());

  static void loadJdbcDriver(final String jdbcDriverClassName)
    throws SchemaCrawlerException
  {
    try
    {
      Class.forName(jdbcDriverClassName);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not load JDBC driver, "
                                       + jdbcDriverClassName, e);
    }
  }

  private Map<String, String> connectionProperties;
  private String user;
  private String password;

  @Override
  public final Connection getConnection()
    throws SQLException
  {
    return getConnection(user, password);
  }

  @Override
  public final Connection getConnection(final String user, final String password)
    throws SQLException
  {
    if (user == null)
    {
      LOGGER.log(Level.WARNING, "Database user is not provided");
    }
    if (password == null)
    {
      LOGGER.log(Level.WARNING, "Database password is not provided");
    }

    String connectionUrl;
    try
    {
      connectionUrl = getConnectionUrl();
    }
    catch (final Exception e)
    {
      throw new SQLException(String.format("Could not connect to database, for user %s",
                                           user),
                             e);
    }

    final Properties jdbcConnectionProperties = new Properties();
    if (connectionProperties != null)
    {
      jdbcConnectionProperties.putAll(connectionProperties);
    }
    if (user != null)
    {
      jdbcConnectionProperties.put("user", user);
    }
    if (password != null)
    {
      jdbcConnectionProperties.put("password", password);
    }
    try
    {
      LOGGER.log(Level.INFO, String
        .format("Making connection to %s, with user %s", connectionUrl, user));
      final Connection connection = DriverManager
        .getConnection(connectionUrl, jdbcConnectionProperties);
      return connection;
    }
    catch (final SQLException e)
    {
      jdbcConnectionProperties.remove("password");
      throw new SchemaCrawlerSQLException(String.format("Could not connect to %s, with properties %s",
                                                        connectionUrl,
                                                        jdbcConnectionProperties),
                                          e);
    }
  }

  public Map<String, String> getConnectionProperties()
  {
    return connectionProperties;
  }

  @Override
  public final Driver getJdbcDriver()
  {
    try
    {
      return DriverManager.getDriver(getConnectionUrl());
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING,
                 "Could not get a database driver for database connection URL "
                     + getConnectionUrl());
      return null;
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

  public void setConnectionProperties(final Map<String, String> connectionProperties)
  {
    this.connectionProperties = connectionProperties;
  }

  public void setConnectionProperties(final String connectionPropertiesString)
  {
    connectionProperties = new HashMap<String, String>();
    if (!Utility.isBlank(connectionPropertiesString))
    {
      for (final String property: connectionPropertiesString.split(";"))
      {
        if (!Utility.isBlank(property))
        {
          final String[] propertyValues = property.split("=");
          if (propertyValues.length >= 1)
          {
            final String key = propertyValues[0];
            final String value;
            if (propertyValues.length >= 2)
            {
              value = propertyValues[1];
            }
            else
            {
              value = null;
            }
            connectionProperties.put(key, value);
          }
        }
      }
    }
  }

  @Override
  public void setLoginTimeout(final int seconds)
    throws SQLException
  {
    throw new SQLException("Not implemented");
  }

  @Override
  public void setLogWriter(final PrintWriter out)
    throws SQLException
  {
    throw new SQLException("Not implemented");
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
    final StringBuilder builder = new StringBuilder();
    builder.append("driver=").append(getJdbcDriver().getClass().getName())
      .append(Utility.NEWLINE);
    builder.append("url=").append(getConnectionUrl()).append(Utility.NEWLINE);
    builder.append("user=").append(getUser()).append(Utility.NEWLINE);
    return builder.toString();
  }

  @Override
  public <T> T unwrap(final Class<T> iface)
    throws SQLException
  {
    return null;
  }

}
