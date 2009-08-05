/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.utility.datasource;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.utility.ObjectToString;

/**
 * A DataSource that creates connections by reading a properties file.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
public final class PropertiesDataSource
  implements DataSource
{

  private static final String DRIVER = "driver";
  private static final String URL = "url";
  private static final String USER = "user";
  private static final String PASSWORD = "password";

  private static final String DEFAULTCONNECTION = "defaultconnection";

  private static final Logger LOGGER = Logger
    .getLogger(PropertiesDataSource.class.getName());

  private final Map<String, String> connectionParams;
  private PrintWriter logWriter;

  /**
   * Creates a PropertiesDataSource from a set of connection properties,
   * using the default connection.
   * 
   * @param properties
   *        Connection properties.
   * @throws PropertiesDataSourceException
   *         On any exception in creating the PropertiesDataSource.
   */
  public PropertiesDataSource(final Properties properties)
  {
    this(properties, null);
  }

  /**
   * Creates a PropertiesDataSource from a set of connection properties,
   * using the named connection. If the named connection is null or
   * empty, use the default connection.
   * 
   * @param properties
   *        Connection properties.
   * @param connectionName
   *        The name of the connection to use.
   * @throws PropertiesDataSourceException
   *         On any exception in creating the PropertiesDataSource.
   */
  public PropertiesDataSource(final Properties properties,
                              final String connectionName)
  {
    logWriter = new PrintWriter(System.err);

    connectionParams = getConnectionParameters(properties, connectionName);
    logConnectionParams(connectionParams);
    loadDriver(connectionParams);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#getConnection()
   */
  public Connection getConnection()
    throws SQLException
  {
    final String username = connectionParams.get(USER);
    final String password = connectionParams.get(PASSWORD);
    return getConnection(username, password);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#getConnection(java.lang.String,
   *      java.lang.String)
   */
  public Connection getConnection(final String username, final String password)
    throws SQLException
  {
    final String url = connectionParams.get(URL);
    final Connection connection = DriverManager.getConnection(url,
                                                              username,
                                                              password);
    if (connection == null)
    {
      throw new SQLException("Could not establish a connection");
    }
    logConnectionInfo(connection);
    return connection;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#getLoginTimeout()
   */
  public int getLoginTimeout()
  {
    return 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#getLogWriter()
   */
  public PrintWriter getLogWriter()
  {
    return logWriter;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */
  public boolean isWrapperFor(final Class<?> iface)
    throws SQLException
  {
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#setLoginTimeout(int)
   */
  public void setLoginTimeout(final int seconds)
    throws SQLException
  {
    // Not implemented
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
   */
  public void setLogWriter(final PrintWriter out)
  {
    if (out != null)
    {
      logWriter = out;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
   */
  public <T> T unwrap(final Class<T> iface)
    throws SQLException
  {
    throw new SQLException("Not implemented");
  }

  private Map<String, String> getConnectionParameters(final Properties properties,
                                                      final String connectionName)
  {
    final String defaultConnection = properties.getProperty(DEFAULTCONNECTION,
                                                            "");
    String useConnectionName = connectionName;

    // Get the sub-group of the properties for the given connection
    if (connectionName == null)
    {
      useConnectionName = defaultConnection;
    }

    final GroupedProperties groups = new GroupedProperties(properties);
    // Check if the connection name is defined
    if (!groups.isGroup(useConnectionName))
    {
      throw new IllegalArgumentException("Connection not defined: "
                                         + useConnectionName);
    }

    // Create substituted properties
    final SubstitutableProperties substitutableProperties = new SubstitutableProperties(groups
      .subgroup(useConnectionName));
    // Ensure that the property substitution happens correctly
    final Map<String, String> connectionParams = new HashMap<String, String>();
    for (final Object keyObject: substitutableProperties.keySet())
    {
      final String key = String.valueOf(keyObject);
      connectionParams.put(key, substitutableProperties.getProperty(key));
    }
    return connectionParams;
  }

  private void loadDriver(final Map<String, String> connectionParams)
  {
    try
    {
      final String driver = connectionParams.get(DRIVER);
      Class.forName(driver);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Could not establish a connection", e);
    }
  }

  private void logConnectionInfo(final Connection connection)
  {
    try
    {
      if (LOGGER.isLoggable(Level.INFO))
      {
        final DatabaseMetaData metaData = connection.getMetaData();
        final Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("catalog", connection.getCatalog());
        infoMap.put("database product", String.format("%s %s", metaData
          .getDatabaseProductName(), metaData.getDatabaseProductVersion()));
        infoMap.put("driver", String.format("%s %s",
                                            metaData.getDriverName(),
                                            metaData.getDriverVersion()));

        LOGGER.log(Level.INFO, "Opened database connection, " + connection
                               + ObjectToString.toString(infoMap));
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not obtain connection metadata", e);
    }
  }

  private void logConnectionParams(final Map<String, String> connectionParams)
  {
    if (LOGGER.isLoggable(Level.CONFIG))
    {
      final Map<String, String> connectionParamsMap = new HashMap<String, String>(connectionParams);
      connectionParamsMap.remove(PASSWORD);

      LOGGER.log(Level.CONFIG, "Connection parameters:"
                               + ObjectToString.toString(connectionParamsMap));
    }
  }

}
