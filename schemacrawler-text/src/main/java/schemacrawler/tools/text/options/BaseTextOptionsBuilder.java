/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.options;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import schemacrawler.schemacrawler.IdentifierQuotingStrategy;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;

public abstract class BaseTextOptionsBuilder<
        B extends BaseTextOptionsBuilder<B, O>, O extends BaseTextOptions>
    implements OptionsBuilder<BaseTextOptionsBuilder<B, O>, O>,
        ConfigOptionsBuilder<BaseTextOptionsBuilder<B, O>, O> {

  protected static final String SCHEMACRAWLER_FORMAT_PREFIX = "schemacrawler.format.";

  private static final String NO_SCHEMACRAWLER_INFO =
      SCHEMACRAWLER_FORMAT_PREFIX + "no_schemacrawler_info";
  private static final String SHOW_DATABASE_INFO =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_database_info";
  private static final String SHOW_JDBC_DRIVER_INFO =
      SCHEMACRAWLER_FORMAT_PREFIX + "show_jdbc_driver_info";

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

  private static final String NO_SCHEMA_COLORS = SCHEMACRAWLER_FORMAT_PREFIX + "no_schema_colors";
  private static final String SCHEMA_COLOR_MAP = SCHEMACRAWLER_FORMAT_PREFIX + "color_map";

  private static final String IDENTIFIER_QUOTING_STRATEGY =
      SCHEMACRAWLER_FORMAT_PREFIX + "identifier_quoting_strategy";

  protected boolean isAlphabeticalSortForRoutineParameters;
  protected boolean isAlphabeticalSortForRoutines;
  protected boolean isAlphabeticalSortForTableColumns;
  protected boolean isAlphabeticalSortForTables = true;
  protected boolean isNoSchemaCrawlerInfo;
  protected boolean isShowDatabaseInfo;
  protected boolean isShowJdbcDriverInfo;
  protected boolean isShowUnqualifiedNames;
  protected boolean isNoSchemaColors;
  protected IdentifierQuotingStrategy identifierQuotingStrategy;
  protected DatabaseObjectColorMap colorMap;

  protected BaseTextOptionsBuilder() {
    // All fields are set to the defaults
    this.identifierQuotingStrategy =
        IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words;
    this.colorMap = new DatabaseObjectColorMap(new HashMap<>());
  }

  @Override
  public B fromConfig(final Config map) {
    if (map == null) {
      return (B) this;
    }

    final Config config = new Config(map);

    isShowDatabaseInfo = config.getBooleanValue(SHOW_DATABASE_INFO);
    isShowJdbcDriverInfo = config.getBooleanValue(SHOW_JDBC_DRIVER_INFO);
    isNoSchemaCrawlerInfo = config.getBooleanValue(NO_SCHEMACRAWLER_INFO);

    isShowUnqualifiedNames = config.getBooleanValue(SHOW_UNQUALIFIED_NAMES);

    isAlphabeticalSortForTables =
        config.getBooleanValue(SORT_ALPHABETICALLY_TABLES, isAlphabeticalSortForTables);
    isAlphabeticalSortForTableColumns =
        config.getBooleanValue(
            SORT_ALPHABETICALLY_TABLE_COLUMNS, isAlphabeticalSortForTableColumns);

    isAlphabeticalSortForRoutines =
        config.getBooleanValue(SORT_ALPHABETICALLY_ROUTINES, isAlphabeticalSortForRoutines);
    isAlphabeticalSortForRoutineParameters =
        config.getBooleanValue(
            SORT_ALPHABETICALLY_ROUTINE_PARAMETERS, isAlphabeticalSortForRoutineParameters);

    isNoSchemaColors = config.getBooleanValue(NO_SCHEMA_COLORS);

    identifierQuotingStrategy =
        config.getEnumValue(
            IDENTIFIER_QUOTING_STRATEGY,
            IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words);

    if (isNoSchemaColors) {
      colorMap = new DatabaseObjectColorMap();
    } else {
      final Map<String, Object> subMap = config.getSubMap(SCHEMA_COLOR_MAP);
      if (subMap != null && !subMap.isEmpty()) {
        final Map<String, String> properties = new HashMap<>();
        for (final Entry<String, Object> subMapEntry : subMap.entrySet()) {
          final String key = subMapEntry.getKey();
          final String value = String.valueOf(subMapEntry.getValue());
          properties.put(key, value);
        }
        colorMap = new DatabaseObjectColorMap(properties);
      }
    }

    // Override values from command line
    fromConfigCommandLineOverride(map);

    return (B) this;
  }

  @Override
  public B fromOptions(final O options) {
    if (options == null) {
      return (B) this;
    }

    isShowDatabaseInfo = options.isShowDatabaseInfo();
    isShowJdbcDriverInfo = options.isShowJdbcDriverInfo();
    isNoSchemaCrawlerInfo = options.isNoSchemaCrawlerInfo();

    isShowUnqualifiedNames = options.isShowUnqualifiedNames();

    isAlphabeticalSortForTables = options.isAlphabeticalSortForTables();
    isAlphabeticalSortForTableColumns = options.isAlphabeticalSortForTableColumns();

    isAlphabeticalSortForRoutines = options.isAlphabeticalSortForRoutines();
    isAlphabeticalSortForRoutineParameters = options.isAlphabeticalSortForRoutineParameters();

    isNoSchemaColors = options.isNoSchemaColors();

    identifierQuotingStrategy = options.getIdentifierQuotingStrategy();

    colorMap = options.getColorMap();

    return (B) this;
  }

  /** Corresponds to the -noinfo command-line argument. */
  public final B noInfo() {
    return noInfo(true);
  }

  /** Corresponds to the -noinfo=&lt;boolean&gt; command-line argument. */
  public final B noInfo(final boolean value) {
    isNoSchemaCrawlerInfo = value;
    isShowDatabaseInfo = !value;
    isShowJdbcDriverInfo = !value;
    return (B) this;
  }

  public final B noSchemaColors() {
    return noSchemaColors(true);
  }

  public final B noSchemaColors(final boolean value) {
    isNoSchemaColors = value;
    return (B) this;
  }

  public final B noSchemaCrawlerInfo() {
    return noSchemaCrawlerInfo(true);
  }

  public final B noSchemaCrawlerInfo(final boolean value) {
    isNoSchemaCrawlerInfo = value;
    return (B) this;
  }

  public final B showDatabaseInfo() {
    return showDatabaseInfo(true);
  }

  public final B showDatabaseInfo(final boolean value) {
    isShowDatabaseInfo = value;
    return (B) this;
  }

  public final B showJdbcDriverInfo() {
    return showJdbcDriverInfo(true);
  }

  public final B showJdbcDriverInfo(final boolean value) {
    isShowJdbcDriverInfo = value;
    return (B) this;
  }

  public final B showUnqualifiedNames() {
    return showUnqualifiedNames(true);
  }

  public final B showUnqualifiedNames(final boolean value) {
    isShowUnqualifiedNames = value;
    return (B) this;
  }

  /** Corresponds to the --sort-parameters command-line argument. */
  public final B sortRoutineParameters() {
    return sortRoutineParameters(true);
  }

  /** Corresponds to the --sort-parameters=&lt;boolean&gt; command-line argument. */
  public final B sortRoutineParameters(final boolean value) {
    isAlphabeticalSortForRoutineParameters = value;
    return (B) this;
  }

  public final B sortRoutines() {
    return sortRoutines(true);
  }

  public final B sortRoutines(final boolean value) {
    isAlphabeticalSortForRoutines = value;
    return (B) this;
  }

  /** Corresponds to the --sort-columns command-line argument. */
  public final B sortTableColumns() {
    return sortTableColumns(true);
  }

  /** Corresponds to the --sort-columns=&lt;boolean&gt; command-line argument. */
  public final B sortTableColumns(final boolean value) {
    isAlphabeticalSortForTableColumns = value;
    return (B) this;
  }

  /** Corresponds to the ---sort-tables command-line argument. */
  public final B sortTables() {
    return sortTables(true);
  }

  /** Corresponds to the --sort-tables=&lt;boolean&gt; command-line argument. */
  public final B sortTables(final boolean value) {
    isAlphabeticalSortForTables = value;
    return (B) this;
  }

  @Override
  public Config toConfig() {
    final Config config = new Config();

    config.put(NO_SCHEMACRAWLER_INFO, isNoSchemaCrawlerInfo);
    config.put(SHOW_DATABASE_INFO, isShowDatabaseInfo);
    config.put(SHOW_JDBC_DRIVER_INFO, isShowJdbcDriverInfo);

    config.put(SHOW_UNQUALIFIED_NAMES, isShowUnqualifiedNames);

    config.put(SORT_ALPHABETICALLY_TABLES, isAlphabeticalSortForTables);
    config.put(SORT_ALPHABETICALLY_TABLE_COLUMNS, isAlphabeticalSortForTableColumns);

    config.put(SORT_ALPHABETICALLY_ROUTINES, isAlphabeticalSortForRoutines);

    config.put(SORT_ALPHABETICALLY_ROUTINE_PARAMETERS, isAlphabeticalSortForRoutineParameters);

    config.put(NO_SCHEMA_COLORS, isNoSchemaColors);

    config.put(IDENTIFIER_QUOTING_STRATEGY, identifierQuotingStrategy);

    return config;
  }

  public B withColorMap(final DatabaseObjectColorMap colorMap) {
    if (colorMap == null) {
      this.colorMap = new DatabaseObjectColorMap();
    } else {
      this.colorMap = colorMap;
    }
    return (B) this;
  }

  public final B withIdentifierQuotingStrategy(
      final IdentifierQuotingStrategy identifierQuotingStrategy) {
    if (identifierQuotingStrategy == null) {
      this.identifierQuotingStrategy =
          IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words;
    } else {
      this.identifierQuotingStrategy = identifierQuotingStrategy;
    }
    return (B) this;
  }

  private void fromConfigCommandLineOverride(final Config config) {

    final String noinfoKey = "no-info";
    if (config.containsKey(noinfoKey)) {
      noInfo(config.getBooleanValue(noinfoKey));
    }

    final String sorttablesKey = "sort-tables";
    if (config.containsKey(sorttablesKey)) {
      sortTables(config.getBooleanValue(sorttablesKey));
    }

    final String sortcolumnsKey = "sort-columns";
    if (config.containsKey(sortcolumnsKey)) {
      sortTableColumns(config.getBooleanValue(sortcolumnsKey));
    }

    final String sortroutinesKey = "sort-routines";
    if (config.containsKey(sortroutinesKey)) {
      sortRoutines(config.getBooleanValue(sortroutinesKey));
    }
  }
}
