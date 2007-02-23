/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
package dbconnector.test;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Based on org.hsqldb.test.TestBase by boucherb@users
 */
public class TestUtility
{

  private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";

  private static final Logger LOGGER = Logger.getLogger(TestUtility.class
    .getName());

  private static final String HSQLDB_FILE_URL = "jdbc:hsqldb:file:_distrib/dbserver/schemacrawler;shutdown=true";
  private static final String HSQLDB_SERVER_URL = "jdbc:hsqldb:hsql://localhost/schemacrawler";

  private static final boolean IS_SERVER = true;
  private static final boolean DEBUG = false;

  protected String serverProps;
  protected String url;
  protected String user = "sa";
  protected String password = "";

  protected PropertiesDataSource dataSource;
  protected PrintWriter out;

  public void setUp()
    throws PropertiesDataSourceException, ClassNotFoundException
  {

    LOGGER.log(Level.FINE, toString() + " - Setting up unit tests");
    if (DEBUG)
    {
      Utilities.setApplicationLogLevel(Level.FINEST);
      out = new PrintWriter(System.out, true);
    }
    else
    {
      Utilities.setApplicationLogLevel(Level.OFF);
      out = new PrintWriter(this.new NullWriter(), true);
    }

    if (IS_SERVER)
    {
      url = HSQLDB_SERVER_URL;
    }
    else
    {
      url = HSQLDB_FILE_URL;
    }

    Class.forName(HSQLDB_JDBC_DRIVER);
    makeDataSource(url);
    dataSource.setLogWriter(out);

  }

  public void tearDown()
  {
    LOGGER.log(Level.FINE, toString() + " - Tearing down unit tests");
    closeDataSource();
  }

  public PropertiesDataSource getDataSource()
  {
    return dataSource;
  }

  private synchronized void makeDataSource(final String url)
    throws PropertiesDataSourceException
  {
    final String DATASOURCE_NAME = "schemacrawler";

    final Properties connectionProperties = new Properties();
    connectionProperties.setProperty(DATASOURCE_NAME + ".driver",
                                     HSQLDB_JDBC_DRIVER);
    connectionProperties.setProperty(DATASOURCE_NAME + ".url", url);
    connectionProperties.setProperty(DATASOURCE_NAME + ".user", "sa");
    connectionProperties.setProperty(DATASOURCE_NAME + ".password", "");

    dataSource = new PropertiesDataSource(connectionProperties, DATASOURCE_NAME);
  }

  private synchronized void closeDataSource()
  {
    try
    {
      if (dataSource != null)
      {
        final Connection connection = dataSource.getConnection();
        if (connection != null)
        {
          connection.close();
        }
        dataSource = null;
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "", e);
    }
  }

  private class NullWriter
    extends Writer
  {

    private NullWriter()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void write(final int c)
      throws IOException
    {
    }

    /**
     * {@inheritDoc}
     */
    public void write(final char cbuf[], final int off, final int len)
      throws IOException
    {
    }

    /**
     * {@inheritDoc}
     */
    public void write(final String str, final int off, final int len)
      throws IOException
    {
    }

    /**
     * {@inheritDoc}
     */
    public void flush()
      throws IOException
    {
    }

    /**
     * {@inheritDoc}
     */
    public void close()
      throws IOException
    {
    }

  }

}
