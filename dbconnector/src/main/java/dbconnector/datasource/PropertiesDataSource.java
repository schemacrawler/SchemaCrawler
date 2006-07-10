/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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

package dbconnector.datasource;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import sf.util.GroupedProperties;
import sf.util.LocalClassLoader;
import sf.util.SubstitutableProperties;
import sf.util.Utilities;

/**
 * A DataSource that creates connections by reading a proerties file.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 * @version 1.0
 */
public final class PropertiesDataSource
  implements DataSource
{

  public static final Logger LOGGER = Logger
    .getLogger(PropertiesDataSource.class.getName());

  private String url;
  private Properties connectionParams;
  private int loginTimeout;
  private Driver jdbcDriver;

  private String databaseProductName;
  private String databaseProductVersion;
  private String driverName;
  private String driverVersion;
  private String catalogTerm;
  private String catalog;

  private PrintWriter logWriter;

  static
  {
    Utilities.checkJavaVersion(1.4);
  }

  /**
   * Creates a PropertiesDataSource from a set of connection properties, using
   * the default connection.
   * 
   * @param properties
   *          Connection properties.
   * @throws PropertiesDataSourceException
   *           On any exception in creating the PropertiesDataSource.
   */
  public PropertiesDataSource(final Properties properties)
    throws PropertiesDataSourceException
  {
    this(properties, null);
  }

  /**
   * Creates a PropertiesDataSource from a set of connection properties, using
   * the named connection. If the named connection is null or empty, use the
   * default connection.
   * 
   * @param properties
   *          Connection properties.
   * @param connectionName
   *          The name of the connection to use.
   * @throws PropertiesDataSourceException
   *           On any exception in creating the PropertiesDataSource.
   */
  public PropertiesDataSource(final Properties properties,
                              final String connectionName)
    throws PropertiesDataSourceException
  {

    final String defaultConnection = properties
      .getProperty("defaultconnection", "");
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
      throw new PropertiesDataSourceException("Connection not defined: "
                                              + useConnectionName);
    }

    // create substituted properties
    final SubstitutableProperties substitutedProperties = new SubstitutableProperties(
        groups.subgroup(useConnectionName));

    logWriter = new PrintWriter(System.err);

    connectionParams = substitutedProperties;

    LOGGER.log(Level.FINE, "Using connection \"" + connectionName + "\"");
    LOGGER.log(Level.FINE, getConnectionParamsInfo());

    try
    {
      final LocalClassLoader classLoader = LocalClassLoader.getClassLoader();
      Class.forName("java.sql.DriverManager", true, classLoader);

      // load driver
      final String driver = connectionParams.getProperty("driver");
      final Class jdbcDriverClass = Class.forName(driver, true, classLoader);
      jdbcDriver = (Driver) jdbcDriverClass.newInstance();
    }
    catch (final ClassNotFoundException e)
    {
      throw new PropertiesDataSourceException(e.getLocalizedMessage(), e);
    }
    catch (final InstantiationException e)
    {
      throw new PropertiesDataSourceException(e.getLocalizedMessage(), e);
    }
    catch (final IllegalAccessException e)
    {
      throw new PropertiesDataSourceException(e.getLocalizedMessage(), e);
    }

    url = connectionParams.getProperty("url");

    testConnection();

  }

  private void testConnection()
    throws PropertiesDataSourceException
  {

    LOGGER.log(Level.FINE, "Attempting connection...");

    Connection connection = null;
    try
    {
      connection = getConnection();
      if (connection == null)
      {
        throw new PropertiesDataSourceException(
            "Could not establish a connection");
      }
      // set metadata properties
      final DatabaseMetaData metaData = connection.getMetaData();
      databaseProductName = metaData.getDatabaseProductName();
      databaseProductVersion = metaData.getDatabaseProductVersion();
      driverName = metaData.getDriverName();
      driverVersion = metaData.getDriverVersion();
      catalogTerm = metaData.getCatalogTerm();
      if (catalogTerm == null || catalogTerm.length() == 0)
      {
        catalogTerm = "catalog";
      }
      catalog = connection.getCatalog();
      if (catalog == null)
      {
        catalog = "";
      }
    }
    catch (final SQLException e)
    {
      throw new PropertiesDataSourceException(
          "Could not establish a connection", e);
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
        throw new PropertiesDataSourceException("Could not close connection", e);
      }
    }

    LOGGER.log(Level.FINE, "Connection successful.");
    LOGGER.log(Level.INFO, "\n" + toString());

  }

  private String getConnectionParamsInfo()
  {

    final StringBuffer buffer = new StringBuffer();
    final Enumeration connectionParamsKeys = connectionParams.propertyNames();

    buffer.append("Connection parameters:");
    while (connectionParamsKeys.hasMoreElements())
    {
      final String key = (String) connectionParamsKeys.nextElement();
      final String value = connectionParams.getProperty(key);
      if (!key.equalsIgnoreCase("password"))
      {
        buffer.append(Utilities.NEWLINE).append("-- ").append(key).append(": ")
          .append(value);
      }
    }

    return buffer.toString();

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
   * Gets the name of the JDBC driver class.
   * 
   * @return Name of the JDBC driver class.
   */
  public String getJdbcDriverClass()
  {
    return jdbcDriver.getClass().getName();
  }

  /**
   * Get the username for the database connection.
   * 
   * @return Username for the database connection
   */
  public String getUser()
  {
    return connectionParams.getProperty("user");
  }

  /**
   * Get the catalog for this database connection.
   * 
   * @return Catalog name
   */
  public String getCatalog()
  {
    return catalog;
  }

  /**
   * <p>
   * Attempts to establish a connection with the data source that this
   * <code>DataSource</code> object represents.
   * 
   * @return a connection to the data source
   * @throws SQLException
   *           if a database access error occurs
   */
  public Connection getConnection()
    throws SQLException
  {

    final String username = connectionParams.getProperty("user");
    final String password = connectionParams.getProperty("password");

    return getConnection(username, password);

  }

  /**
   * <p>
   * Attempts to establish a connection with the data source that this
   * <code>DataSource</code> object represents.
   * 
   * @param username
   *          the database user on whose behalf the connection is being made
   * @param password
   *          the user's password
   * @return a connection to the data source
   * @throws SQLException
   *           if a database access error occurs
   */
  public Connection getConnection(final String username, final String password)
    throws SQLException
  {

    if (username == null || password == null)
    {
      throw new SQLException("Null username or password");
    }

    final Properties params = new Properties();

    params.setProperty("user", username);
    params.setProperty("password", password);

    return jdbcDriver.connect(url, params);

  }

  /**
   * Gets the maximum time in seconds that this data source can wait while
   * attempting to connect to a database. A value of zero means that the timeout
   * is the default system timeout if there is one; otherwise, it means that
   * there is no timeout. When a <code>DataSource</code> object is created,
   * the login timeout is initially zero.
   * 
   * @return the data source login time limit
   * @see #setLoginTimeout
   */
  public int getLoginTimeout()
  {
    return loginTimeout;
  }

  /**
   * <p>
   * Sets the maximum time in seconds that this data source will wait while
   * attempting to connect to a database. A value of zero specifies that the
   * timeout is the default system timeout if there is one; otherwise, it
   * specifies that there is no timeout. When a <code>DataSource</code> object
   * is created, the login timeout is initially zero.
   * 
   * @param seconds
   *          the data source login time limit
   * @see #getLoginTimeout
   */
  public void setLoginTimeout(final int seconds)
  {
    loginTimeout = seconds;
  }

  /**
   * <p>
   * Retrieves the log writer for this <code>DataSource</code> object. <p/>
   * <p>
   * The log writer is a character output stream to which all logging and
   * tracing messages for this data source will be printed. This includes
   * messages printed by the methods of this object, messages printed by methods
   * of other objects manufactured by this object, and so on. Messages printed
   * to a data source specific log writer are not printed to the log writer
   * associated with the <code>java.sql.Drivermanager</code> class. When a
   * <code>DataSource</code> object is created, the log writer is initially
   * null; in other words, the default is for logging to be disabled.
   * 
   * @return the log writer for this data source or null if logging is disabled
   * @see #setLogWriter
   */
  public PrintWriter getLogWriter()
  {
    return logWriter;
  }

  /**
   * <p>
   * Sets the log writer for this <code>DataSource</code> object to the given
   * <code>java.io.PrintWriter</code> object. <p/>
   * <p>
   * The log writer is a character output stream to which all logging and
   * tracing messages for this data source will be printed. This includes
   * messages printed by the methods of this object, messages printed by methods
   * of other objects manufactured by this object, and so on. Messages printed
   * to a data source- specific log writer are not printed to the log writer
   * associated with the <code>java.sql.Drivermanager</code> class. When a
   * <code>DataSource</code> object is created the log writer is initially
   * null; in other words, the default is for logging to be disabled.
   * 
   * @param out
   *          the new log writer; to disable logging, set to null
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
   * Gets the properties that were used to create this data source.
   * 
   * @return Source properties
   */
  public Properties getSourceProperties()
  {
    return new Properties(connectionParams);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {

    final StringBuffer info = new StringBuffer();

    info.append("-- database product: ").append(databaseProductName)
      .append(" ").append(databaseProductVersion).append(Utilities.NEWLINE)
      .append("-- driver: ").append(jdbcDriver.getClass().getName())
      .append(" - ").append(driverName).append(" ").append(driverVersion)
      .append(Utilities.NEWLINE).append("-- connection: ").append(url)
      .append(Utilities.NEWLINE).append("-- " + catalogTerm + ": ")
      .append(catalog);

    return info.toString();

  } // end databaseInfo

}
