/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * A retriever that uses JDBC driver metadata to get the details about
 * the JDBC driver.
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
      driverInfo.setJdbcCompliant(jdbcDriver.jdbcCompliant());

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
      LOGGER.log(Level.WARNING, "Could not obtain JDBC driver information", e);
    }

    return driverInfo;
  }

}
