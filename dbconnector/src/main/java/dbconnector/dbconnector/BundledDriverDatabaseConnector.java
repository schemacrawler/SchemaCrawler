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
package dbconnector.dbconnector;


import javax.sql.DataSource;

import sf.util.CommandLineParser;
import sf.util.Config;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import dbconnector.datasource.PropertiesDataSource;

/**
 * Parses a command line, and create a data-source.
 */
public final class BundledDriverDatabaseConnector
  implements DatabaseConnector
{

  private static final String OPTION_HOST = "host";
  private static final String OPTION_PORT = "port";
  private static final String OPTION_DATABASE = "database";
  private static final String OPTION_USER = "user";
  private static final String OPTION_PASSWORD = "password";

  private final Config configResource;
  private final String dataSourceName;

  /**
   * Creates the command line parser, and stored the .
   * 
   * @param args
   *        Command line arguments
   * @param baseConfigResource
   *        Base resource
   * @throws DatabaseConnectorException
   */
  public BundledDriverDatabaseConnector(final String[] args,
                                        final Config baseConfigResource)
    throws DatabaseConnectorException
  {
    if (baseConfigResource == null)
    {
      throw new DatabaseConnectorException("Bundled driver needs configuration");
    }
    configResource = baseConfigResource;

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String host = parser.getStringOptionValue(OPTION_HOST);
    final String port = parser.getStringOptionValue(OPTION_PORT);
    final String database = parser.getStringOptionValue(OPTION_DATABASE);
    final String user = parser.getStringOptionValue(OPTION_USER);
    final String password = parser.getStringOptionValue(OPTION_PASSWORD);

    dataSourceName = configResource.get("defaultconnection");
    if (user != null && password != null)
    {
      if (host != null)
      {
        configResource.put(dataSourceName + ".host", host);
      }
      if (port != null)
      {
        configResource.put(dataSourceName + ".port", port);
      }
      configResource.put(dataSourceName + ".database", database);
      configResource.put(dataSourceName + ".user", user);
      configResource.put(dataSourceName + ".password", password);
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
    return new PropertiesDataSource(configResource.toProperties());
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

    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_HOST, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_PORT, null));
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_DATABASE, ""));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_USER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_PASSWORD,
                                      null));

    return parser;
  }

}
