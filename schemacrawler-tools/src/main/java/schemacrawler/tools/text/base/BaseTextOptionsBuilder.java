/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConfigOptionsBuilder;
import schemacrawler.schemacrawler.IdentifierQuotingStrategy;
import schemacrawler.schemacrawler.OptionsBuilder;

public abstract class BaseTextOptionsBuilder<B extends BaseTextOptionsBuilder<B, O>, O extends BaseTextOptions>
  implements OptionsBuilder<BaseTextOptionsBuilder<B, O>, O>, ConfigOptionsBuilder<BaseTextOptionsBuilder<B, O>, O>
{

  protected static final String SCHEMACRAWLER_FORMAT_PREFIX =
    "schemacrawler.format.";

  private static final String NO_HEADER =
    SCHEMACRAWLER_FORMAT_PREFIX + "no_header";
  private static final String NO_FOOTER =
    SCHEMACRAWLER_FORMAT_PREFIX + "no_footer";
  private static final String NO_SCHEMACRAWLER_INFO =
    SCHEMACRAWLER_FORMAT_PREFIX + "no_schemacrawler_info";
  private static final String SHOW_DATABASE_INFO =
    SCHEMACRAWLER_FORMAT_PREFIX + "show_database_info";
  private static final String SHOW_JDBC_DRIVER_INFO =
    SCHEMACRAWLER_FORMAT_PREFIX + "show_jdbc_driver_info";
  private static final String APPEND_OUTPUT =
    SCHEMACRAWLER_FORMAT_PREFIX + "append_output";

  private static final String SHOW_UNQUALIFIED_NAMES =
    SCHEMACRAWLER_FORMAT_PREFIX + "show_unqualified_names";

  private static final String SORT_ALPHABETICALLY_TABLES =
    SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.tables";
  private static final String SORT_ALPHABETICALLY_TABLE_COLUMNS =
    SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.table_columns";

  private static final String SORT_ALPHABETICALLY_ROUTINES =
    SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.routines";
  private static final String SORT_ALPHABETICALLY_ROUTINE_PARAMETERS =
    SCHEMACRAWLER_FORMAT_PREFIX + "sort_alphabetically.routine_columns";

  private static final String NO_SCHEMA_COLORS =
    SCHEMACRAWLER_FORMAT_PREFIX + "no_schema_colors";

  private static final String IDENTIFIER_QUOTING_STRATEGY =
    SCHEMACRAWLER_FORMAT_PREFIX + "identifier_quoting_strategy";

  protected boolean isAlphabeticalSortForRoutineParameters;
  protected boolean isAlphabeticalSortForRoutines;
  protected boolean isAlphabeticalSortForTableColumns;
  protected boolean isAlphabeticalSortForTables = true;
  protected boolean isAppendOutput;
  protected boolean isNoFooter;
  protected boolean isNoHeader;
  protected boolean isNoSchemaCrawlerInfo;
  protected boolean isShowDatabaseInfo;
  protected boolean isShowJdbcDriverInfo;
  protected boolean isShowUnqualifiedNames;
  protected boolean isNoSchemaColors;
  protected IdentifierQuotingStrategy identifierQuotingStrategy;

  protected BaseTextOptionsBuilder()
  {
    // All fields are set to the defaults
  }

  public final B appendOutput()
  {
    isAppendOutput = true;
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

    isNoFooter = config.getBooleanValue(NO_FOOTER);
    isNoHeader = config.getBooleanValue(NO_HEADER);
    isShowDatabaseInfo = config.getBooleanValue(SHOW_DATABASE_INFO);
    isShowJdbcDriverInfo = config.getBooleanValue(SHOW_JDBC_DRIVER_INFO);
    isNoSchemaCrawlerInfo = config.getBooleanValue(NO_SCHEMACRAWLER_INFO);
    isAppendOutput = config.getBooleanValue(APPEND_OUTPUT);

    isShowUnqualifiedNames = config.getBooleanValue(SHOW_UNQUALIFIED_NAMES);

    isAlphabeticalSortForTables = config.getBooleanValue(
      SORT_ALPHABETICALLY_TABLES,
      isAlphabeticalSortForTables);
    isAlphabeticalSortForTableColumns = config.getBooleanValue(
      SORT_ALPHABETICALLY_TABLE_COLUMNS,
      isAlphabeticalSortForTableColumns);

    isAlphabeticalSortForRoutines = config.getBooleanValue(
      SORT_ALPHABETICALLY_ROUTINES,
      isAlphabeticalSortForRoutines);
    isAlphabeticalSortForRoutineParameters = config.getBooleanValue(
      SORT_ALPHABETICALLY_ROUTINE_PARAMETERS,
      isAlphabeticalSortForRoutineParameters);

    isNoSchemaColors = config.getBooleanValue(NO_SCHEMA_COLORS);

    identifierQuotingStrategy = config.getEnumValue(IDENTIFIER_QUOTING_STRATEGY,
                                                    IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words);

    return (B) this;
  }

  @Override
  public B fromOptions(final O options)
  {
    if (options == null)
    {
      return (B) this;
    }

    isNoFooter = options.isNoFooter();
    isNoHeader = options.isNoHeader();
    isShowDatabaseInfo = options.isShowDatabaseInfo();
    isShowJdbcDriverInfo = options.isShowJdbcDriverInfo();
    isNoSchemaCrawlerInfo = options.isNoSchemaCrawlerInfo();
    isAppendOutput = options.isAppendOutput();

    isShowUnqualifiedNames = options.isShowUnqualifiedNames();

    isAlphabeticalSortForTables = options.isAlphabeticalSortForTables();
    isAlphabeticalSortForTableColumns =
      options.isAlphabeticalSortForTableColumns();

    isAlphabeticalSortForRoutines = options.isAlphabeticalSortForRoutines();
    isAlphabeticalSortForRoutineParameters =
      options.isAlphabeticalSortForRoutineParameters();

    isNoSchemaColors = options.isNoSchemaColors();

    identifierQuotingStrategy = options.getIdentifierQuotingStrategy();

    return (B) this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = new Config();

    config.setBooleanValue(NO_FOOTER, isNoFooter);
    config.setBooleanValue(NO_HEADER, isNoHeader);
    config.setBooleanValue(NO_SCHEMACRAWLER_INFO, isNoSchemaCrawlerInfo);
    config.setBooleanValue(SHOW_DATABASE_INFO, isShowDatabaseInfo);
    config.setBooleanValue(SHOW_JDBC_DRIVER_INFO, isShowJdbcDriverInfo);
    config.setBooleanValue(APPEND_OUTPUT, isAppendOutput);

    config.setBooleanValue(SHOW_UNQUALIFIED_NAMES, isShowUnqualifiedNames);

    config.setBooleanValue(SORT_ALPHABETICALLY_TABLES,
                           isAlphabeticalSortForTables);
    config.setBooleanValue(SORT_ALPHABETICALLY_TABLE_COLUMNS,
                           isAlphabeticalSortForTableColumns);

    config.setBooleanValue(SORT_ALPHABETICALLY_ROUTINES,
                           isAlphabeticalSortForRoutines);

    config.setBooleanValue(SORT_ALPHABETICALLY_ROUTINE_PARAMETERS,
                           isAlphabeticalSortForRoutineParameters);

    config.setBooleanValue(NO_SCHEMA_COLORS, isNoSchemaColors);

    config.setEnumValue(IDENTIFIER_QUOTING_STRATEGY, identifierQuotingStrategy);

    return config;
  }

  public final B noFooter()
  {
    return noFooter(true);
  }

  public final B noFooter(final boolean value)
  {
    isNoFooter = value;
    return (B) this;
  }

  public final B noHeader()
  {
    return noHeader(true);
  }

  public final B noHeader(final boolean value)
  {
    isNoHeader = value;
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
    isNoSchemaCrawlerInfo = value;
    isShowDatabaseInfo = !value;
    isShowJdbcDriverInfo = !value;
    return (B) this;
  }

  public final B noSchemaColors()
  {
    return noSchemaColors(true);
  }

  public final B noSchemaColors(final boolean value)
  {
    isNoSchemaColors = value;
    return (B) this;
  }

  public final B noSchemaCrawlerInfo()
  {
    return noSchemaCrawlerInfo(true);
  }

  public final B noSchemaCrawlerInfo(final boolean value)
  {
    isNoSchemaCrawlerInfo = value;
    return (B) this;
  }

  public final B overwriteOutput()
  {
    isAppendOutput = false;
    return (B) this;
  }

  public final B showDatabaseInfo()
  {
    return showDatabaseInfo(true);
  }

  public final B showDatabaseInfo(final boolean value)
  {
    isShowDatabaseInfo = value;
    return (B) this;
  }

  public final B showJdbcDriverInfo()
  {
    return showJdbcDriverInfo(true);
  }

  public final B showJdbcDriverInfo(final boolean value)
  {
    isShowJdbcDriverInfo = value;
    return (B) this;
  }

  public final B showUnqualifiedNames()
  {
    return showUnqualifiedNames(true);
  }

  public final B showUnqualifiedNames(final boolean value)
  {
    isShowUnqualifiedNames = value;
    return (B) this;
  }

  /**
   * Corresponds to the --sort-parameters command-line argument.
   */
  public final B sortRoutineParameters()
  {
    return sortRoutineParameters(true);
  }

  /**
   * Corresponds to the --sort-parameters=&lt;boolean&gt; command-line
   * argument.
   */
  public final B sortRoutineParameters(final boolean value)
  {
    isAlphabeticalSortForRoutineParameters = value;
    return (B) this;
  }

  public final B sortRoutines()
  {
    return sortRoutines(true);
  }

  public final B sortRoutines(final boolean value)
  {
    isAlphabeticalSortForRoutines = value;
    return (B) this;
  }

  /**
   * Corresponds to the --sort-columns command-line argument.
   */
  public final B sortTableColumns()
  {
    return sortTableColumns(true);
  }

  /**
   * Corresponds to the --sort-columns=&lt;boolean&gt; command-line argument.
   */
  public final B sortTableColumns(final boolean value)
  {
    isAlphabeticalSortForTableColumns = value;
    return (B) this;
  }

  /**
   * Corresponds to the ---sort-tables command-line argument.
   */
  public final B sortTables()
  {
    return sortTables(true);
  }

  /**
   * Corresponds to the --sort-tables=&lt;boolean&gt; command-line argument.
   */
  public final B sortTables(final boolean value)
  {
    isAlphabeticalSortForTables = value;
    return (B) this;
  }

  public final B withIdentifierQuotingStrategy(final IdentifierQuotingStrategy identifierQuotingStrategy)
  {
    if (identifierQuotingStrategy == null)
    {
      this.identifierQuotingStrategy = IdentifierQuotingStrategy.quote_none;
    }
    else
    {
      this.identifierQuotingStrategy = identifierQuotingStrategy;
    }
    return (B) this;
  }

}
