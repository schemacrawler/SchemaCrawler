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
package schemacrawler.main;


import java.util.Properties;

import schemacrawler.crawl.SchemaCrawlerException;
import sf.util.CommandLineParser;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Parses a command line, and create a data-source.
 */
public final class BundledDriverDataSourceParser
{

  private static final String OPTION_HOST = "host";
  private static final String OPTION_PORT = "port";
  private static final String OPTION_DATABASE = "database";
  private static final String OPTION_USER = "user";
  private static final String OPTION_PASSWORD = "password";

  private final Properties configResource;

  /**
   * Creates the command line parser, and stored the .
   * 
   * @param args
   * @param baseConfigResource
   * @throws SchemaCrawlerException
   */
  public BundledDriverDataSourceParser(final String[] args,
                                       final Properties baseConfigResource)
    throws SchemaCrawlerException
  {
    if (baseConfigResource == null)
    {
      throw new SchemaCrawlerException("Bundled driver needs configuration");
    }
    configResource = baseConfigResource;

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String host = parser.getStringOptionValue(OPTION_HOST);
    final String port = parser.getStringOptionValue(OPTION_PORT);
    final String database = parser.getStringOptionValue(OPTION_DATABASE);
    final String user = parser.getStringOptionValue(OPTION_USER);
    final String password = parser.getStringOptionValue(OPTION_PASSWORD);

    final String connectionName = configResource
      .getProperty("defaultconnection");
    if (user != null && password != null)
    {
      configResource.setProperty(connectionName + ".host", host);
      configResource.setProperty(connectionName + ".port", port);
      configResource.setProperty(connectionName + ".database", database);
      configResource.setProperty(connectionName + ".user", user);
      configResource.setProperty(connectionName + ".password", password);
    }
  }

  /**
   * Creates a new bundled data source from the bundled driver
   * properties.
   * 
   * @return Data source
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public PropertiesDataSource createDataSource()
    throws SchemaCrawlerException
  {
    PropertiesDataSource dataSource;
    try
    {
      dataSource = new PropertiesDataSource(configResource);
    }
    catch (final PropertiesDataSourceException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }

    return dataSource;
  }

  private CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_HOST,
                                      "localhost"));
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_PORT, "1433"));
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_DATABASE, ""));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_USER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_PASSWORD,
                                      null));

    return parser;
  }

}
