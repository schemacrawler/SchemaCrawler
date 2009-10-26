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

package schemacrawler.utility;


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

  private static final Logger LOGGER = Logger
    .getLogger(PropertiesDataSource.class.getName());

  private final Properties properties;
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
    logWriter = new PrintWriter(System.err);

    if (properties == null)
    {
      throw new IllegalArgumentException("No connection properties provided");
    }
    this.properties = properties;
    logConnectionParams();
    loadDriver();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.sql.DataSource#getConnection()
   */
  public Connection getConnection()
    throws SQLException
  {
    final String username = properties.getProperty(USER);
    final String password = properties.getProperty(PASSWORD);
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
    final String url = properties.getProperty(URL);
    Connection connection;
    try
    {
      connection = DriverManager.getConnection(url, username, password);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not establish a connection with \""
                                + url + "\": " + e.getMessage());
      throw e;
    }
    if (connection == null)
    {
      throw new SQLException("Could not establish a connection with \"" + url
                             + "\"");
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

  private void loadDriver()
  {
    final String driver = properties.getProperty(DRIVER);
    try
    {
      Class.forName(driver);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Could not load driver class, " + driver, e);
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

  private void logConnectionParams()
  {
    if (LOGGER.isLoggable(Level.CONFIG))
    {
      final Properties connectionParamsMap = new Properties(properties);
      connectionParamsMap.remove(PASSWORD);

      LOGGER.log(Level.CONFIG, "Connection parameters:"
                               + ObjectToString.toString(connectionParamsMap));
    }
  }

}
