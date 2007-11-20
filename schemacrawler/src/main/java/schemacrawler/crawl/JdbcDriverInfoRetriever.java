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

package schemacrawler.crawl;


import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SchemaRetriever uses JDBC driver metadata to get the details about
 * the schema.
 * 
 * @author Sualeh Fatehi
 */
final class JdbcDriverInfoRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(JdbcDriverInfoRetriever.class.getName());

  JdbcDriverInfoRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Provides information on the JDBC driver.
   * 
   * @return JDBC driver information
   * @throws SQLException
   *         On a SQL exception
   */
  MutableJdbcDriverInfo retrieveJdbcDriverInfo()
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = getRetrieverConnection().getMetaData();
    final String url = dbMetaData.getURL();

    final MutableJdbcDriverInfo driverInfo = new MutableJdbcDriverInfo();

    driverInfo.setDriverName(dbMetaData.getDriverName());
    driverInfo.setDriverVersion(dbMetaData.getDriverVersion());
    driverInfo.setConnectionUrl(url);

    try
    {
      final Driver jdbcDriver = DriverManager.getDriver(url);
      driverInfo.setJdbcDriverClassName(jdbcDriver.getClass().getName());
      final DriverPropertyInfo[] propertyInfo = jdbcDriver
        .getPropertyInfo(url, new Properties());
      for (final DriverPropertyInfo driverPropertyInfo: propertyInfo)
      {
        driverInfo
          .addJdbcDriverProperty(new MutableJdbcDriverProperty(driverPropertyInfo));
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.FINE, "Could not obtain JDBC driver information", e);
    }

    return driverInfo;
  }

}
