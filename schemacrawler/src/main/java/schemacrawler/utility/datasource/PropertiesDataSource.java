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
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * A DataSource that creates connections by reading a properties file.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
public final class PropertiesDataSource
  implements DataSource
{

  /**
   * System specific line separator character.
   */
  private static final String NEWLINE = System.getProperty("line.separator");

  private static final String DRIVER = "driver";
  private static final String URL = "url";
  private static final String USER = "user";
  private static final String PASSWORD = "password";

  private static final String DEFAULTCONNECTION = "defaultconnection";

  private static final Logger LOGGER = Logger
    .getLogger(PropertiesDataSource.class.getName());

  private String url;
  private Properties connectionParams;
  private int loginTimeout;
  private Driver jdbcDriver;

  private String databaseProductName;
  private String databaseProductVersion;
  private String driverName;
  private String driverVersion;

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
    constructPropertiesDataSource(properties, null);
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
    constructPropertiesDataSource(properties, connectionName);
  }

  /**
   * Attempts to establish a connection with the data source that this
   * <code>DataSource</code> object represents.
   * 
   * @return a connection to the data source
   * @throws SQLException
   *         if a database access error occurs
   */
  public Connection getConnection()
    throws SQLException
  {

    final String username = connectionParams.getProperty(USER);
    final String password = connectionParams.getProperty(PASSWORD);

    return getConnection(username, password);

  }

  /**
   * Attempts to establish a connection with the data source that this
   * <code>DataSource</code> object represents.
   * 
   * @param username
   *        the database user on whose behalf the connection is being
   *        made
   * @param password
   *        the user's password
   * @return a connection to the data source
   * @throws SQLException
   *         if a database access error occurs
   */
  public Connection getConnection(final String username, final String password)
    throws SQLException
  {

    if (username == null || password == null)
    {
      throw new SQLException("Null username or password");
    }

    final Properties params = new Properties();

    params.setProperty(USER, username);
    params.setProperty(PASSWORD, password);

    return jdbcDriver.connect(url, params);

  }

  /**
   * Gets the name of the JDBC driver class.
   * 
   * @return Name of the JDBC driver class.
   */
  public String getJdbcDriverClass()
  {
    return jdbcDriver.getClass().getName();
  }

  /**
   * Gets the maximum time in seconds that this data source can wait
   * while attempting to connect to a database. A value of zero means
   * that the timeout is the default system timeout if there is one;
   * otherwise, it means that there is no timeout. When a
   * <code>DataSource</code> object is created, the login timeout is
   * initially zero.
   * 
   * @return the data source login time limit
   * @see #setLoginTimeout
   */
  public int getLoginTimeout()
  {
    return loginTimeout;
  }

  /**
   * Retrieves the log writer for this <code>DataSource</code> object.
   * The log writer is a character output stream to which all logging
   * and tracing messages for this data source will be printed. This
   * includes messages printed by the methods of this object, messages
   * printed by methods of other objects manufactured by this object,
   * and so on. Messages printed to a data source specific log writer
   * are not printed to the log writer associated with the
   * <code>java.sql.Drivermanager</code> class. When a
   * <code>DataSource</code> object is created, the log writer is
   * initially null; in other words, the default is for logging to be
   * disabled.
   * 
   * @return the log writer for this data source or null if logging is
   *         disabled
   * @see #setLogWriter
   */
  public PrintWriter getLogWriter()
  {
    return logWriter;
  }

  /**
   * Gets the database connection URL.
   * 
   * @return Database connection URL
   */
  public String getUrl()
  {
    return url;
  }

  /**
   * Get the username for the database connection.
   * 
   * @return Username for the database connection
   */
  public String getUser()
  {
    return connectionParams.getProperty(USER);
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
   * Sets the maximum time in seconds that this data source will wait
   * while attempting to connect to a database. A value of zero
   * specifies that the timeout is the default system timeout if there
   * is one; otherwise, it specifies that there is no timeout. When a
   * <code>DataSource</code> object is created, the login timeout is
   * initially zero.
   * 
   * @param seconds
   *        the data source login time limit
   * @see #getLoginTimeout
   */
  public void setLoginTimeout(final int seconds)
  {
    loginTimeout = seconds;
  }

  /**
   * Sets the log writer for this <code>DataSource</code> object to the
   * given <code>java.io.PrintWriter</code> object. The log writer is a
   * character output stream to which all logging and tracing messages
   * for this data source will be printed. This includes messages
   * printed by the methods of this object, messages printed by methods
   * of other objects manufactured by this object, and so on. Messages
   * printed to a data source- specific log writer are not printed to
   * the log writer associated with the
   * <code>java.sql.Drivermanager</code> class. When a
   * <code>DataSource</code> object is created the log writer is
   * initially null; in other words, the default is for logging to be
   * disabled.
   * 
   * @param out
   *        the new log writer; to disable logging, set to null
   * @see #getLogWriter
   */
  public void setLogWriter(final PrintWriter out)
  {
    if (out != null)
    {
      logWriter = out;
      // DriverManager.setLogWriter(out);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {

    final StringBuilder info = new StringBuilder();
    info.append("-- database product: ").append(databaseProductName)
      .append(" ").append(databaseProductVersion).append(NEWLINE)
      .append("-- driver: ").append(jdbcDriver.getClass().getName())
      .append(" - ").append(driverName).append(" ").append(driverVersion)
      .append(NEWLINE).append("-- connection: ").append(url);
    return info.toString();

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

  private void constructPropertiesDataSource(final Properties properties,
                                             final String connectionName)
  {
    final String defaultConnection = properties.getProperty(DEFAULTCONNECTION,
                                                            "");
    String useConnectionName = connectionName;

    // get the subgroup of the properties for the given connection
    if (connectionName == null)
    {
      useConnectionName = defaultConnection;
    }

    final GroupedProperties groups = new GroupedProperties(properties);

    // check if the connection name is defined
    if (!groups.isGroup(useConnectionName))
    {
      throw new IllegalArgumentException("Connection not defined: "
                                         + useConnectionName);
    }

    // create substituted properties
    final SubstitutableProperties substitutedProperties = new SubstitutableProperties(groups
      .subgroup(useConnectionName));

    logWriter = new PrintWriter(System.err);

    connectionParams = substitutedProperties;

    LOGGER.log(Level.FINE, "Using connection \"" + connectionName + "\"");
    LOGGER.log(Level.FINE, getConnectionParamsInfo());

    try
    {
      final String driver = connectionParams.getProperty(DRIVER);
      final Class<?> jdbcDriverClass = Class.forName(driver);
      jdbcDriver = (Driver) jdbcDriverClass.newInstance();
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Driver class not be initialized - "
                                 + e.getLocalizedMessage(), e);
    }

    url = connectionParams.getProperty(URL);

    testConnection();
  }

  private String getConnectionParamsInfo()
  {

    final StringBuilder buffer = new StringBuilder();
    buffer.append("Connection parameters:");
    final Set<Map.Entry<Object, Object>> entries = connectionParams.entrySet();
    for (final Map.Entry<Object, Object> entry: entries)
    {
      final String key = (String) entry.getKey();
      final String value = (String) entry.getValue();
      if (!key.equalsIgnoreCase(PASSWORD))
      {
        buffer.append(NEWLINE).append("-- ").append(key).append(": ")
          .append(value);
      }
    }

    return buffer.toString();

  }

  private void testConnection()
  {

    LOGGER.log(Level.FINEST, "Attempting connection...");

    Connection connection = null;
    try
    {
      connection = getConnection();
      if (connection == null)
      {
        throw new RuntimeException("Could not establish a connection");
      }
      // set metadata properties
      final DatabaseMetaData metaData = connection.getMetaData();
      databaseProductName = metaData.getDatabaseProductName();
      databaseProductVersion = metaData.getDatabaseProductVersion();
      driverName = metaData.getDriverName();
      driverVersion = metaData.getDriverVersion();
    }
    catch (final SQLException e)
    {
      final String errorMessage = e.getMessage();
      LOGGER.log(Level.SEVERE, "Could not establish a connection: "
                               + errorMessage);
      throw new RuntimeException(errorMessage, e);
    }
    finally
    {
      try
      {
        if (connection != null)
        {
          connection.close();
        }
      }
      catch (final SQLException e)
      {
        final String errorMessage = e.getMessage();
        LOGGER.log(Level.WARNING, "Could not close the connection: "
                                  + errorMessage);
        throw new RuntimeException(errorMessage, e);
      }
    }

    LOGGER.log(Level.FINE, "Database connection opened - " + connection);
    LOGGER.log(Level.INFO, NEWLINE + toString());

  }

}
