/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.text.base;


import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.utility.IdentifierQuotingStrategy;

public abstract class BaseTextOptionsBuilder<B extends BaseTextOptionsBuilder<B, O>, O extends BaseTextOptions>
  implements OptionsBuilder<BaseTextOptionsBuilder<B, O>, O>
{

  protected static final String SCHEMACRAWLER_FORMAT_PREFIX = "schemacrawler.format.";

  private static final String NO_HEADER = SCHEMACRAWLER_FORMAT_PREFIX
                                          + "no_header";
  private static final String NO_FOOTER = SCHEMACRAWLER_FORMAT_PREFIX
                                          + "no_footer";
  private static final String NO_SCHEMACRAWLER_INFO = SCHEMACRAWLER_FORMAT_PREFIX
                                                      + "no_schemacrawler_info";
  private static final String SHOW_DATABASE_INFO = SCHEMACRAWLER_FORMAT_PREFIX
                                                   + "show_database_info";
  private static final String SHOW_JDBC_DRIVER_INFO = SCHEMACRAWLER_FORMAT_PREFIX
                                                      + "show_jdbc_driver_info";
  private static final String APPEND_OUTPUT = SCHEMACRAWLER_FORMAT_PREFIX
                                              + "append_output";

  private static final String SHOW_UNQUALIFIED_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "show_unqualified_names";

  private static final String SORT_ALPHABETICALLY_TABLES = SCHEMACRAWLER_FORMAT_PREFIX
                                                           + "sort_alphabetically.tables";
  private static final String SORT_ALPHABETICALLY_TABLE_COLUMNS = SCHEMACRAWLER_FORMAT_PREFIX
                                                                  + "sort_alphabetically.table_columns";

  private static final String SORT_ALPHABETICALLY_ROUTINES = SCHEMACRAWLER_FORMAT_PREFIX
                                                             + "sort_alphabetically.routines";
  private static final String SORT_ALPHABETICALLY_ROUTINE_COLUMNS = SCHEMACRAWLER_FORMAT_PREFIX
                                                                    + "sort_alphabetically.routine_columns";

  private static final String NO_SCHEMA_COLORS = SCHEMACRAWLER_FORMAT_PREFIX
                                                 + "no_schema_colors";

  private static final String IDENTIFIER_QUOTING_STRATEGY = SCHEMACRAWLER_FORMAT_PREFIX
                                                            + "identifier_quoting_strategy";

  protected final O options;

  protected BaseTextOptionsBuilder(final O options)
  {
    this.options = requireNonNull(options, "No options provided");
  }

  public final B appendOutput()
  {
    options.setAppendOutput(true);
    return (B) this;
  }

  @Override
  public B fromConfig(final Config map)
  {
    if (map == null)
    {
      return (B) this;
    }

    final Config config = new Config(map);

    options.setNoFooter(config.getBooleanValue(NO_FOOTER));
    options.setNoHeader(config.getBooleanValue(NO_HEADER));
    options.setShowDatabaseInfo(config.getBooleanValue(SHOW_DATABASE_INFO));
    options
      .setShowJdbcDriverInfo(config.getBooleanValue(SHOW_JDBC_DRIVER_INFO));
    options
      .setNoSchemaCrawlerInfo(config.getBooleanValue(NO_SCHEMACRAWLER_INFO));
    options.setAppendOutput(config.getBooleanValue(APPEND_OUTPUT));

    options
      .setShowUnqualifiedNames(config.getBooleanValue(SHOW_UNQUALIFIED_NAMES));

    options.setAlphabeticalSortForTables(config
      .getBooleanValue(SORT_ALPHABETICALLY_TABLES,
                       options.isAlphabeticalSortForTables()));
    options.setAlphabeticalSortForTableColumns(config
      .getBooleanValue(SORT_ALPHABETICALLY_TABLE_COLUMNS,
                       options.isAlphabeticalSortForTableColumns()));

    options.setAlphabeticalSortForRoutines(config
      .getBooleanValue(SORT_ALPHABETICALLY_ROUTINES,
                       options.isAlphabeticalSortForRoutines()));

    options.setAlphabeticalSortForRoutineColumns(config
      .getBooleanValue(SORT_ALPHABETICALLY_ROUTINE_COLUMNS,
                       options.isAlphabeticalSortForRoutineColumns()));

    options.setNoSchemaColors(config.getBooleanValue(NO_SCHEMA_COLORS));

    options.setIdentifierQuotingStrategy(config
      .getEnumValue(IDENTIFIER_QUOTING_STRATEGY,
                    IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words));

    return (B) this;
  }

  public final B noFooter()
  {
    return noFooter(true);
  }

  public final B noFooter(final boolean value)
  {
    options.setNoFooter(value);
    return (B) this;
  }

  public final B noHeader()
  {
    return noHeader(true);
  }

  public final B noHeader(final boolean value)
  {
    options.setNoHeader(value);
    return (B) this;
  }

  /**
   * Corresponds to the -noinfo command-line argument.
   */
  public final B noInfo()
  {
    return noInfo(true);
  }

  /**
   * Corresponds to the -noinfo=&lt;boolean&gt; command-line argument.
   */
  public final B noInfo(final boolean value)
  {
    options.setNoSchemaCrawlerInfo(value);
    options.setShowDatabaseInfo(!value);
    options.setShowJdbcDriverInfo(!value);
    return (B) this;
  }

  public final B noSchemaColors()
  {
    return noSchemaColors(true);
  }

  public final B noSchemaColors(final boolean value)
  {
    options.setNoSchemaColors(value);
    return (B) this;
  }

  public final B noSchemaCrawlerInfo()
  {
    return noSchemaCrawlerInfo(true);
  }

  public final B noSchemaCrawlerInfo(final boolean value)
  {
    options.setNoSchemaCrawlerInfo(value);
    return (B) this;
  }

  public final B overwriteOutput()
  {
    options.setAppendOutput(false);
    return (B) this;
  }

  public final B showDatabaseInfo()
  {
    return showDatabaseInfo(true);
  }

  public final B showDatabaseInfo(final boolean value)
  {
    options.setShowDatabaseInfo(value);
    return (B) this;
  }

  public final B showJdbcDriverInfo()
  {
    return showJdbcDriverInfo(true);
  }

  public final B showJdbcDriverInfo(final boolean value)
  {
    options.setShowJdbcDriverInfo(value);
    return (B) this;
  }

  public final B showUnqualifiedNames()
  {
    return showUnqualifiedNames(true);
  }

  public final B showUnqualifiedNames(final boolean value)
  {
    options.setShowUnqualifiedNames(value);
    return (B) this;
  }

  /**
   * Corresponds to the -sortinout command-line argument.
   */
  public final B sortInOut()
  {
    return sortInOut(true);
  }

  /**
   * Corresponds to the -sortinout=&lt;boolean&gt; command-line
   * argument.
   */
  public final B sortInOut(final boolean value)
  {
    options.setAlphabeticalSortForRoutineColumns(value);
    return (B) this;
  }

  public final B sortRoutines()
  {
    return sortRoutines(true);
  }

  public final B sortRoutines(final boolean value)
  {
    options.setAlphabeticalSortForRoutines(value);
    return (B) this;
  }

  /**
   * Corresponds to the -sortcolumns command-line argument.
   */
  public final B sortTableColumns()
  {
    return sortTableColumns(true);
  }

  /**
   * Corresponds to the -sortcolumns=&lt;boolean&gt; command-line
   * argument.
   */
  public final B sortTableColumns(final boolean value)
  {
    options.setAlphabeticalSortForTableColumns(value);
    return (B) this;
  }

  /**
   * Corresponds to the -sorttables command-line argument.
   */
  public final B sortTables()
  {
    return sortTables(true);
  }

  /**
   * Corresponds to the -sorttables=&lt;boolean&gt; command-line
   * argument.
   */
  public final B sortTables(final boolean value)
  {
    options.setAlphabeticalSortForTables(value);
    return (B) this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = new Config();

    config.setBooleanValue(NO_FOOTER, options.isNoFooter());
    config.setBooleanValue(NO_HEADER, options.isNoHeader());
    config.setBooleanValue(NO_SCHEMACRAWLER_INFO,
                           options.isNoSchemaCrawlerInfo());
    config.setBooleanValue(SHOW_DATABASE_INFO, options.isShowDatabaseInfo());
    config.setBooleanValue(SHOW_JDBC_DRIVER_INFO,
                           options.isShowJdbcDriverInfo());
    config.setBooleanValue(APPEND_OUTPUT, options.isAppendOutput());

    config.setBooleanValue(SHOW_UNQUALIFIED_NAMES,
                           options.isShowUnqualifiedNames());

    config.setBooleanValue(SORT_ALPHABETICALLY_TABLES,
                           options.isAlphabeticalSortForTables());
    config.setBooleanValue(SORT_ALPHABETICALLY_TABLE_COLUMNS,
                           options.isAlphabeticalSortForTableColumns());

    config.setBooleanValue(SORT_ALPHABETICALLY_ROUTINES,
                           options.isAlphabeticalSortForRoutines());

    config.setBooleanValue(SORT_ALPHABETICALLY_ROUTINE_COLUMNS,
                           options.isAlphabeticalSortForRoutineColumns());

    config.setBooleanValue(NO_SCHEMA_COLORS, options.isNoSchemaColors());

    config.setEnumValue(IDENTIFIER_QUOTING_STRATEGY,
                        options.getIdentifierQuotingStrategy());

    return config;
  }

  @Override
  public O toOptions()
  {
    return (O) options;
  }

  @Override
  public String toString()
  {
    return options.toString();
  }

  public final B withIdentifierQuotingStrategy(final IdentifierQuotingStrategy identifierQuotingStrategy)
  {
    options.setIdentifierQuotingStrategy(identifierQuotingStrategy);
    return (B) this;
  }

}
