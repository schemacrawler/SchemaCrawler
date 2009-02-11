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

package schemacrawler.main.dbconnector;


import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.sql.DataSource;

import schemacrawler.utility.datasource.PropertiesDataSource;

/**
 * Parses a command line, and creates a data-source.
 */
abstract class BaseDatabaseConnector
  implements DatabaseConnector
{

  private final Map<String, String> config;

  /**
   * Parses a command line, and creates a data-source.
   * 
   * @param args
   *        Command line arguments
   * @param providedConfig
   *        Connection properties
   * @throws DatabaseConnectorException
   *         On an exception
   */
  protected BaseDatabaseConnector(final Map<String, String> providedConfig)
    throws DatabaseConnectorException
  {
    if (providedConfig == null)
    {
      throw new DatabaseConnectorException("No configuration provided");
    }
    // Don't make a copy of the config that was loaded in from the
    // classpath resource
    config = providedConfig;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#createDataSource()
   */
  public final DataSource createDataSource()
    throws DatabaseConnectorException
  {
    try
    {
      final Properties properties = new Properties();
      for (final Entry<String, String> entry: config.entrySet())
      {
        properties.setProperty(entry.getKey(), entry.getValue());
      }
      return new PropertiesDataSource(properties);
    }
    catch (final Exception e)
    {
      throw new DatabaseConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#getDataSourceName()
   */
  public String getDataSourceName()
  {
    return config.get("defaultconnection");
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#hasDataSourceName()
   */
  public boolean hasDataSourceName()
  {
    final String dataSourceName = getDataSourceName();
    return dataSourceName != null && dataSourceName.trim().length() > 0;
  }

  protected void configPut(String key, String value)
  {
    config.put(key, value);
  }

}
