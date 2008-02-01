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
package schemacrawler.main.dbconnector;


import java.util.Map;

import javax.sql.DataSource;

import schemacrawler.utility.datasource.PropertiesDataSource;
import schemacrawler.utility.datasource.PropertiesDataSourceException;
import sf.util.CommandLineParser;
import sf.util.Utilities;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses a command line, and creates a data-source.
 */
public final class BundledDriverDatabaseConnector
  implements DatabaseConnector
{

  private static final String OPTION_HOST = "host";
  private static final String OPTION_PORT = "port";
  private static final String OPTION_DATABASE = "database";
  private static final String OPTION_SCHEMAPATTERN = "schemapattern";
  private static final String OPTION_USER = "user";
  private static final String OPTION_PASSWORD = "password";

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
  public BundledDriverDatabaseConnector(final String[] args,
                                        final Map<String, String> providedConfig)
    throws DatabaseConnectorException
  {
    if (providedConfig == null)
    {
      throw new DatabaseConnectorException("Bundled driver needs configuration");
    }
    config = providedConfig;

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String host = parser.getStringOptionValue(OPTION_HOST);
    final String port = parser.getStringOptionValue(OPTION_PORT);
    final String database = parser.getStringOptionValue(OPTION_DATABASE);
    final String schemapattern = parser
      .getStringOptionValue(OPTION_SCHEMAPATTERN);
    final String user = parser.getStringOptionValue(OPTION_USER);
    final String password = parser.getStringOptionValue(OPTION_PASSWORD);

    dataSourceName = config.get("defaultconnection");
    if (user != null && password != null)
    {
      if (host != null)
      {
        config.put(dataSourceName + ".host", host);
      }
      if (port != null)
      {
        config.put(dataSourceName + ".port", port);
      }
      config.put(dataSourceName + ".database", database);
      if (!Utilities.isBlank(schemapattern))
      {
        config.put(dataSourceName + ".schemapattern", schemapattern);
      }
      config.put(dataSourceName + ".user", user);
      config.put(dataSourceName + ".password", password);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.main.dbconnector.DatabaseConnector#createDataSource()
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
   * @see schemacrawler.main.dbconnector.DatabaseConnector#getDataSourceName()
   */
  public String getDataSourceName()
  {
    return dataSourceName;
  }

  private CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_HOST, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_PORT, null));
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_DATABASE, ""));
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
