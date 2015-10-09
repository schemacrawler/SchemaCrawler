/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.offline.jdbc;


import static sf.util.Utility.isBlank;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OfflineDriver
  implements Driver
{

  private static final Logger LOGGER = Logger
    .getLogger(OfflineDriver.class.getName());

  private static final String JDBC_URL_PREFIX = "jdbc:offline:";

  static
  {
    try
    {
      DriverManager.registerDriver(new OfflineDriver());
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.SEVERE, "Cannot register offline driver", e);
    }
  }

  @Override
  public boolean acceptsURL(final String url)
    throws SQLException
  {
    return !isBlank(url) && url.startsWith(JDBC_URL_PREFIX);
  }

  @Override
  public Connection connect(final String url, final Properties info)
    throws SQLException
  {
    if (acceptsURL(url))
    {
      final String path = url.substring(JDBC_URL_PREFIX.length());
      return new OfflineConnection(Paths.get(path));
    }
    else
    {
      return null;
    }
  }

  @Override
  public int getMajorVersion()
  {
    return 0;
  }

  @Override
  public int getMinorVersion()
  {
    return 0;
  }

  @Override
  public Logger getParentLogger()
    throws SQLFeatureNotSupportedException
  {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(final String url,
                                              final Properties info)
                                                throws SQLException
  {
    return new DriverPropertyInfo[0];
  }

  @Override
  public boolean jdbcCompliant()
  {
    return false;
  }

}
