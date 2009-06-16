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

/**
 * Parses a command line, and creates a data-source.
 */
public final class PropertiesDataSourceDatabaseConnector
  extends BaseDatabaseConnector
{

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
  public PropertiesDataSourceDatabaseConnector(final String[] args,
                                               final Map<String, String> providedConfig)
    throws DatabaseConnectorException
  {
    super(providedConfig);

    final PropertiesDataSourceOptions options = new PropertiesDataSourceOptionsParser(args)
      .getOptions();

    if (options.isUseJdbcConnection())
    {
      final String dataSourceName = "PropertiesDataSourceConnection";
      configPut("defaultconnection", dataSourceName);
      configPut(dataSourceName + ".driver", options.getDriver());
      configPut(dataSourceName + ".url", options.getConnectionUrl());
      if (options.hasSchemaPattern())
      {
        configPut(dataSourceName + ".schemapattern", options.getSchemapattern());
      }
      configPut(dataSourceName + ".user", options.getUser());
      configPut(dataSourceName + ".password", options.getPassword());
    }
    else
    {
      final String connectionName = options.getConnection();
      // Use default connection if no connection is specified
      if (!options.isUseDefaultConnection()
          && !schemacrawler.utility.Utility.isBlank(connectionName))
      {
        configPut("defaultconnection", connectionName);
      }
    }

    if (!hasDataSourceName())
    {
      throw new DatabaseConnectorException("No datasource name provided");
    }
  }

}
