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

package schemacrawler.tools.grep;


import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.InclusionRule;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.main.CommandLineUtility;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextFormatter;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineParser;
import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSource;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class ColumnsGrep
{

  private static final String OPTION_LOG_LEVEL = "log-level";

  private static final String OPTION_INCLUDE_TABLES = "tables";
  private static final String OPTION_INCLUDE_COLUMNS = "columns";
  private static final String OPTION_INVERT_MATCH = "invert-match";

  private static final String OPTION_CONFIGFILE = "configfile";
  private static final String OPTION_CONFIGOVERRIDEFILE = "configoverridefile";

  private ColumnsGrep()
  {
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
  public static void grep(final String[] args)
    throws Exception
  {

    final CommandLineParser parser = parseCommandLine(args);

    String logLevelString = CommandLineUtility.getStringOption(parser
      .getOption(OPTION_LOG_LEVEL), "OFF");
    Level logLevel = Level.parse(logLevelString.toUpperCase(Locale.ENGLISH));
    Utilities.setApplicationLogLevel(logLevel);

    final String cfgFile = CommandLineUtility.getStringOption(parser
      .getOption(OPTION_CONFIGFILE), "schemacrawler.config.properties");
    final String cfgOverrideFile = CommandLineUtility
      .getStringOption(parser.getOption(OPTION_CONFIGOVERRIDEFILE),
                       "schemacrawler.config.override.properties");
    final Properties config = CommandLineUtility
      .loadConfig(cfgFile, cfgOverrideFile);
    final PropertiesDataSource dataSource = dbconnector.Main
      .createDataSource(args, config);

    final String includeTables = CommandLineUtility.getStringOption(parser
      .getOption(OPTION_INCLUDE_TABLES), InclusionRule.INCLUDE_ALL);
    final InclusionRule tableInclusionRule = new InclusionRule(includeTables,
                                                               InclusionRule.EXCLUDE_NONE);

    final String includeColumns = CommandLineUtility.getStringOption(parser
      .getOption(OPTION_INCLUDE_COLUMNS), InclusionRule.INCLUDE_ALL);
    final InclusionRule columnInclusionRule = new InclusionRule(includeColumns,
                                                                InclusionRule.EXCLUDE_NONE);

    final boolean invertMatch = CommandLineUtility.getBooleanOption(parser
      .getOption(OPTION_INVERT_MATCH));

    // Create the options
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(false);
    options.setTableInclusionRule(tableInclusionRule);
    options.setAlphabeticalSortForTableColumns(true);

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(new Properties(),
                                                                      new OutputOptions("text",
                                                                                        null),
                                                                      SchemaTextDetailType.BASIC);

    CrawlHandler formatter = new SchemaTextFormatter(schemaTextOptions,
                                                     columnInclusionRule,
                                                     invertMatch);

    final SchemaCrawler crawler = new SchemaCrawler(dataSource, null, formatter);
    crawler.crawl(options);

  }

  private static CommandLineParser parseCommandLine(final String[] args)
  {
    final CommandLineParser parser = new CommandLineParser();

    parser
      .addOption(new CommandLineParser.StringOption('g', OPTION_CONFIGFILE));
    parser
      .addOption(new CommandLineParser.StringOption('p',
                                                    OPTION_CONFIGOVERRIDEFILE));

    parser.addOption(new CommandLineParser.StringOption(OPTION_INCLUDE_TABLES));
    parser
      .addOption(new CommandLineParser.StringOption(OPTION_INCLUDE_COLUMNS));
    parser.addOption(new CommandLineParser.BooleanOption('v',
                                                         OPTION_INVERT_MATCH));

    parser.addOption(new CommandLineParser.StringOption(OPTION_LOG_LEVEL));

    parser.parse(args);
    return parser;
  }

}
