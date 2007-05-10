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
package schemacrawler.tools.sqlserver;


import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.InclusionRule;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.main.Config;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextFormatter;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineParser;
import sf.util.CommandLineUtility;
import sf.util.Utilities;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Main class that takes arguments for grep-ping table and columns in a
 * schema.
 */
public final class Grep
{
  private static final Logger LOGGER = Logger.getLogger(Grep.class.getName());

  private static final String OPTION_LOG_LEVEL = "log-level";

  private static final String OPTION_HOST = "host";
  private static final String OPTION_PORT = "port";
  private static final String OPTION_DATABASE = "database";
  private static final String OPTION_USER = "user";
  private static final String OPTION_PASSWORD = "password";

  private static final String OPTION_INCLUDE_TABLES = "tables";
  private static final String OPTION_INCLUDE_COLUMNS = "columns";
  private static final String OPTION_INVERT_MATCH = "invert-match";

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    CommandLineUtility.checkForHelp(args, "/schemacrawler-grep-readme.txt");
    grep(args);
  }

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_HOST,
                                      "localhost"));
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_PORT, "1433"));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_DATABASE,
                                      null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_USER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_PASSWORD,
                                      null));

    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_INCLUDE_TABLES,
                                      InclusionRule.INCLUDE_ALL));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_INCLUDE_COLUMNS,
                                      InclusionRule.INCLUDE_ALL));
    parser.addOption(new BooleanOption('v', OPTION_INVERT_MATCH));

    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_LOG_LEVEL,
                                      "OFF"));

    return parser;
  }

  private static PropertiesDataSource createDataSource(final CommandLineParser parser)
    throws SchemaCrawlerException
  {

    final Properties config = Utilities.loadProperties(Grep.class
      .getResourceAsStream("/schemacrawler.config.properties"));
    final String connectionName = config.getProperty("defaultconnection");

    final String host = parser.getStringOptionValue(OPTION_HOST);
    final String port = parser.getStringOptionValue(OPTION_PORT);
    final String database = parser.getStringOptionValue(OPTION_DATABASE);
    final String user = parser.getStringOptionValue(OPTION_USER);
    final String password = parser.getStringOptionValue(OPTION_PASSWORD);

    config.setProperty(connectionName + ".host", host);
    config.setProperty(connectionName + ".port", port);
    config.setProperty(connectionName + ".database", database);
    config.setProperty(connectionName + ".user", user);
    config.setProperty(connectionName + ".password", password);

    PropertiesDataSource dataSource;
    try
    {
      dataSource = new PropertiesDataSource(config);
    }
    catch (final PropertiesDataSourceException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }

    return dataSource;
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  private static void grep(final String[] args)
    throws Exception
  {

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String logLevelString = parser.getStringOptionValue(OPTION_LOG_LEVEL);
    final Level logLevel = Level.parse(logLevelString
      .toUpperCase(Locale.ENGLISH));
    Utilities.setApplicationLogLevel(logLevel);

    final PropertiesDataSource dataSource = createDataSource(parser);

    final String includeTables = parser
      .getStringOptionValue(OPTION_INCLUDE_TABLES);
    final InclusionRule tableInclusionRule = new InclusionRule(includeTables,
                                                               InclusionRule.EXCLUDE_NONE);

    final String includeColumns = parser
      .getStringOptionValue(OPTION_INCLUDE_COLUMNS);
    final InclusionRule columnInclusionRule = new InclusionRule(includeColumns,
                                                                InclusionRule.EXCLUDE_NONE);

    final boolean invertMatch = parser
      .getBooleanOptionValue(OPTION_INVERT_MATCH);

    // Create the options
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(false);
    options.setTableInclusionRule(tableInclusionRule);

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(new Config(),
                                                                      new OutputOptions("text",
                                                                                        null),
                                                                      SchemaTextDetailType.MAXIMUM);

    final CrawlHandler formatter = new SchemaTextFormatter(schemaTextOptions,
                                                           columnInclusionRule,
                                                           invertMatch);

    final SchemaCrawler crawler = new SchemaCrawler(dataSource, null, formatter);
    crawler.crawl(options);

  }

  private Grep()
  {
    // Prevent instantiation
  }

}
