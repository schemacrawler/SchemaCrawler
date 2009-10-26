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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Map.Entry;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.utility.PropertiesDataSource;

/**
 * Parses a command line, and creates a data-source.
 */
abstract class BaseDatabaseConnector
  implements DatabaseConnector
{

  private final Config config;
  private DataSource dataSource;

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
  protected BaseDatabaseConnector(final Config providedConfig)
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
   * @see schemacrawler.main.dbconnector.DatabaseConnector#createConnection()
   */
  public final Connection createConnection()
    throws DatabaseConnectorException
  {
    try
    {
      createDataSource();
      return dataSource.getConnection();
    }
    catch (final SQLException e)
    {
      throw new DatabaseConnectorException(e);
    }
  }

  protected void configPut(final String key, final String value)
  {
    config.put(key, value);
  }

  protected void substituteVariables()
  {
    config.substituteVariables();
  }

  private void createDataSource()
    throws DatabaseConnectorException
  {
    if (dataSource == null)
    {
      try
      {
        final Properties properties = new Properties();
        for (final Entry<String, String> entry: config.entrySet())
        {
          final String key = entry.getKey();
          final String value = entry.getValue();
          if (key != null && value != null)
          {
            properties.setProperty(key, value);
          }
        }
        dataSource = new PropertiesDataSource(properties);
      }
      catch (final Exception e)
      {
        throw new DatabaseConnectorException(e);
      }
    }
  }

}
