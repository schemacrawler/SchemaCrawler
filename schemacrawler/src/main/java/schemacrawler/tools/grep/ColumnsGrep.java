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


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.InclusionRule;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.crawl.SchemaInfoLevel;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextFormatter;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineParser;
import sf.util.Config;
import dbconnector.dbconnector.DatabaseConnector;

/**
 * Provides functionality for grep-ping table and columns in a schema.
 */
public final class ColumnsGrep
{

  private static final String OPTION_INCLUDE_TABLES = "tables";
  private static final String OPTION_INCLUDE_COLUMNS = "columns";
  private static final String OPTION_INVERT_MATCH = "invert-match";

  /**
   * Gets tables that contain the specified columns.
   * 
   * @param dataSource
   *        Data source
   * @param additionalConnectionConfiguration
   *        Additional connection configuration for INFORMATION_SCHEMA
   * @param tableInclusionRule
   *        Inclusion rule for tables
   * @param columnInclusionRule
   *        Inclusion rule for columns
   * @param invertMatch
   *        Whether to invert the table match
   * @return Matching tables
   */
  public static Table[] grep(final DataSource dataSource,
                             final Config additionalConnectionConfiguration,
                             final InclusionRule tableInclusionRule,
                             final InclusionRule columnInclusionRule,
                             final boolean invertMatch)
  {
    final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
    options.setShowStoredProcedures(false);
    options.setTableInclusionRule(tableInclusionRule);

    final Schema schema = SchemaCrawler
      .getSchema(dataSource,
                 additionalConnectionConfiguration,
                 SchemaInfoLevel.basic,
                 options);

    final List<Table> tablesList = new ArrayList<Table>();
    final Table[] allTables = schema.getTables();
    for (final Table table: allTables)
    {
      if (includesColumn(table, columnInclusionRule, invertMatch))
      {
        tablesList.add(table);
      }
    }

    return tablesList.toArray(new Table[tablesList.size()]);
  }

  /**
   * Gets tables that contain the specified columns.
   * 
   * @param dataSource
   *        Data source
   * @param tableInclusionRule
   *        Inclusion rule for tables
   * @param columnInclusionRule
   *        Inclusion rule for columns
   * @param invertMatch
   *        Whether to invert the table match
   * @return Matching tables
   */
  public static Table[] grep(final DataSource dataSource,
                             final InclusionRule tableInclusionRule,
                             final InclusionRule columnInclusionRule,
                             final boolean invertMatch)
  {
    return grep(dataSource,
                null,
                tableInclusionRule,
                columnInclusionRule,
                invertMatch);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @param dataSourceParser
   *        Datasource parser
   * @throws Exception
   *         On an exception
   */
  public static void grep(final String[] args,
                          final DatabaseConnector dataSourceParser)
    throws Exception
  {

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

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
                                                                      SchemaTextDetailType.verbose_schema);

    final CrawlHandler formatter = new SchemaTextFormatter(schemaTextOptions,
                                                           columnInclusionRule,
                                                           invertMatch);
    final DataSource dataSource = dataSourceParser.createDataSource();

    final SchemaCrawler crawler = new SchemaCrawler(dataSource, null, formatter);
    crawler.crawl(options);

  }

  /**
   * Special case for "grep" like functionality. Handle table if a table
   * column inclusion rule is found, and at least one column matches the
   * rule.
   * 
   * @param table
   *        Table to check
   * @param columnInclusionRule
   *        Inclusion rule for columns
   * @param invertMatch
   *        Whether to invert the table match
   * @return Whether the column should be included
   */
  public static boolean includesColumn(final Table table,
                                       final InclusionRule columnInclusionRule,
                                       final boolean invertMatch)
  {
    if (table == null)
    {
      return false;
    }
    if (columnInclusionRule == null)
    {
      return true;
    }

    boolean handleTable = false;
    final Column[] columns = table.getColumns();
    for (final Column column: columns)
    {
      if (columnInclusionRule.include(column.getFullName()))
      {
        // We found a column that should be included, so handle the
        // table
        handleTable = true;
        break;
      }
    }
    if (invertMatch)
    {
      handleTable = !handleTable;
    }
    return handleTable;
  }

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_INCLUDE_TABLES,
                                                    InclusionRule.INCLUDE_ALL));
    parser
      .addOption(new CommandLineParser.StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                    OPTION_INCLUDE_COLUMNS,
                                                    InclusionRule.INCLUDE_ALL));
    parser.addOption(new CommandLineParser.BooleanOption('v',
                                                         OPTION_INVERT_MATCH));

    return parser;
  }

  private ColumnsGrep()
  {
    // Prevent instantiation
  }

}
