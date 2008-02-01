/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package dbconnector.dbconnector;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import sf.util.CommandLineParser;
import sf.util.Utilities;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Parses a command line, and creates a data-source.
 */
public final class PropertiesDataSourceDatabaseConnector
  implements DatabaseConnector
{

  private static final String OPTION_DRIVER = "driver";
  private static final String OPTION_URL = "url";
  private static final String OPTION_SCHEMAPATTERN = "schemapattern";
  private static final String OPTION_USER = "user";
  private static final String OPTION_PASSWORD = "password";

  private static final String OPTION_CONNECTION = "connection";
  private static final String OPTION_DEFAULT = "default";

  private final Map<String, String> config;
  private final String dataSourceName;

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
    if (providedConfig == null)
    {
      config = new HashMap<String, String>();
    }
    else
    {
      config = providedConfig;
    }

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String driver = parser.getStringOptionValue(OPTION_DRIVER);
    final String url = parser.getStringOptionValue(OPTION_URL);
    final String schemapattern = parser
      .getStringOptionValue(OPTION_SCHEMAPATTERN);
    final String user = parser.getStringOptionValue(OPTION_USER);
    final String password = parser.getStringOptionValue(OPTION_PASSWORD);
    final boolean useJdbcConnection = !Utilities.isBlank(driver)
                                      && !Utilities.isBlank(url);

    if (useJdbcConnection)
    {
      dataSourceName = "PropertiesDataSourceConnection";
      config.put(dataSourceName + ".driver", driver);
      config.put(dataSourceName + ".url", url);
      if (!Utilities.isBlank(schemapattern))
      {
        config.put(dataSourceName + ".schemapattern", schemapattern);
      }
      config.put(dataSourceName + ".user", user);
      config.put(dataSourceName + ".password", password);
    }
    else
    {
      final boolean useDefaultConnection = parser
        .getBooleanOptionValue(OPTION_DEFAULT);
      final String connectionName = parser
        .getStringOptionValue(OPTION_CONNECTION);
      // Use default connection if no connection is specified
      if (!useDefaultConnection && !Utilities.isBlank(connectionName))
      {
        config.put("defaultconnection", connectionName);
      }
      dataSourceName = config.get("defaultconnection");
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see dbconnector.dbconnector.DatabaseConnector#createDataSource()
   */
  public DataSource createDataSource()
    throws DatabaseConnectorException
  {
    try
    {
      return new PropertiesDataSource(Utilities.toProperties(config));
    }
    catch (final PropertiesDataSourceException e)
    {
      throw new DatabaseConnectorException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see dbconnector.dbconnector.DatabaseConnector#getDataSourceName()
   */
  public String getDataSourceName()
  {
    return dataSourceName;
  }

  private CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new BooleanOption('d', OPTION_DEFAULT));
    parser.addOption(new StringOption('c', OPTION_CONNECTION, null));
    //
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_DRIVER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_URL, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_SCHEMAPATTERN,
                                      null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_USER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_PASSWORD,
                                      null));
    return parser;
  }

}
