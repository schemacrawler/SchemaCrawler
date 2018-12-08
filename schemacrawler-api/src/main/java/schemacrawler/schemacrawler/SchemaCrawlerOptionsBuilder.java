/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.schemacrawler;


import static sf.util.Utility.enumValue;
import static sf.util.Utility.isBlank;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import schemacrawler.schema.RoutineType;

/**
 * SchemaCrawler options.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptionsBuilder
  implements OptionsBuilder<SchemaCrawlerOptionsBuilder, SchemaCrawlerOptions>
{

  private static final String SC_SCHEMA_PATTERN_EXCLUDE = "schemacrawler.schema.pattern.exclude";
  private static final String SC_SCHEMA_PATTERN_INCLUDE = "schemacrawler.schema.pattern.include";
  private static final String SC_SYNONYM_PATTERN_EXCLUDE = "schemacrawler.synonym.pattern.exclude";
  private static final String SC_SYNONYM_PATTERN_INCLUDE = "schemacrawler.synonym.pattern.include";
  private static final String SC_SEQUENCE_PATTERN_EXCLUDE = "schemacrawler.sequence.pattern.exclude";
  private static final String SC_SEQUENCE_PATTERN_INCLUDE = "schemacrawler.sequence.pattern.include";

  private static final String SC_TABLE_PATTERN_EXCLUDE = "schemacrawler.table.pattern.exclude";
  private static final String SC_TABLE_PATTERN_INCLUDE = "schemacrawler.table.pattern.include";
  private static final String SC_COLUMN_PATTERN_EXCLUDE = "schemacrawler.column.pattern.exclude";
  private static final String SC_COLUMN_PATTERN_INCLUDE = "schemacrawler.column.pattern.include";

  private static final String SC_ROUTINE_PATTERN_EXCLUDE = "schemacrawler.routine.pattern.exclude";
  private static final String SC_ROUTINE_PATTERN_INCLUDE = "schemacrawler.routine.pattern.include";
  private static final String SC_ROUTINE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.routine.inout.pattern.exclude";
  private static final String SC_ROUTINE_COLUMN_PATTERN_INCLUDE = "schemacrawler.routine.inout.pattern.include";

  private static final String SC_GREP_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.column.pattern.include";
  private static final String SC_GREP_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.column.pattern.exclude";
  private static final String SC_GREP_ROUTINE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.routine.inout.pattern.exclude";
  private static final String SC_GREP_ROUTINE_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.routine.inout.pattern.include";
  private static final String SC_GREP_DEFINITION_PATTERN_EXCLUDE = "schemacrawler.grep.definition.pattern.exclude";
  private static final String SC_GREP_DEFINITION_PATTERN_INCLUDE = "schemacrawler.grep.definition.pattern.include";

  public static SchemaCrawlerOptionsBuilder builder()
  {
    return new SchemaCrawlerOptionsBuilder();
  }

  public static SchemaCrawlerOptionsBuilder builder(final SchemaCrawlerOptions options)
  {
    return new SchemaCrawlerOptionsBuilder().fromOptions(options);
  }

  public static SchemaCrawlerOptions newSchemaCrawlerOptions()
  {
    return new SchemaCrawlerOptionsBuilder().toOptions();
  }

  public static SchemaCrawlerOptions newSchemaCrawlerOptions(final Config config)
  {
    return new SchemaCrawlerOptionsBuilder().fromConfig(config).toOptions();
  }

  public static SchemaCrawlerOptions withMaximumSchemaInfoLevel()
  {
    return new SchemaCrawlerOptionsBuilder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum().toOptions())
      .toOptions();
  }

  private static Collection<RoutineType> allRoutineTypes()
  {
    return EnumSet.of(RoutineType.procedure, RoutineType.function);
  }

  private static Collection<String> defaultTableTypes()
  {
    return Arrays.asList("BASE TABLE", "TABLE", "VIEW");
  }

  private SchemaInfoLevel schemaInfoLevel;
  private String title;

  private InclusionRule schemaInclusionRule;
  private InclusionRule synonymInclusionRule;
  private InclusionRule sequenceInclusionRule;
  private Optional<Collection<String>> tableTypes;

  private String tableNamePattern;
  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;

  private Optional<Collection<RoutineType>> routineTypes;
  private InclusionRule routineInclusionRule;

  private InclusionRule routineColumnInclusionRule;
  private Optional<InclusionRule> grepColumnInclusionRule;
  private Optional<InclusionRule> grepRoutineColumnInclusionRule;
  private Optional<InclusionRule> grepDefinitionInclusionRule;
  private boolean grepInvertMatch;

  private boolean grepOnlyMatching;

  private boolean isNoEmptyTables;
  private int childTableFilterDepth;
  private int parentTableFilterDepth;

  /**
   * Default options.
   */
  private SchemaCrawlerOptionsBuilder()
  {
    schemaInfoLevel = SchemaInfoLevelBuilder.standard().toOptions();

    title = "";

    // All schemas are included by default
    schemaInclusionRule = new IncludeAll();

    synonymInclusionRule = new ExcludeAll();
    sequenceInclusionRule = new ExcludeAll();

    // Note: Of the database objects, only tables are included by
    // default
    tableTypes = Optional.of(defaultTableTypes());
    tableInclusionRule = new IncludeAll();
    columnInclusionRule = new IncludeAll();

    routineTypes = Optional.of(allRoutineTypes());
    routineInclusionRule = new ExcludeAll();
    routineColumnInclusionRule = new ExcludeAll();

    grepColumnInclusionRule = Optional.empty();
    grepRoutineColumnInclusionRule = Optional.empty();
    grepDefinitionInclusionRule = Optional.empty();
  }

  public SchemaCrawlerOptionsBuilder childTableFilterDepth(final int childTableFilterDepth)
  {
    if (childTableFilterDepth < 0)
    {
      this.childTableFilterDepth = 0;
    }
    else
    {
      this.childTableFilterDepth = childTableFilterDepth;
    }
    return this;
  }

  /**
   * Options from properties.
   *
   * @param config
   *        Configuration properties
   */
  @Override
  public SchemaCrawlerOptionsBuilder fromConfig(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = new Config(config);
    }

    schemaInclusionRule = configProperties
      .getInclusionRule(SC_SCHEMA_PATTERN_INCLUDE, SC_SCHEMA_PATTERN_EXCLUDE);
    synonymInclusionRule = configProperties
      .getInclusionRuleDefaultExclude(SC_SYNONYM_PATTERN_INCLUDE,
                                      SC_SYNONYM_PATTERN_EXCLUDE);
    sequenceInclusionRule = configProperties
      .getInclusionRuleDefaultExclude(SC_SEQUENCE_PATTERN_INCLUDE,
                                      SC_SEQUENCE_PATTERN_EXCLUDE);

    tableInclusionRule = configProperties
      .getInclusionRule(SC_TABLE_PATTERN_INCLUDE, SC_TABLE_PATTERN_EXCLUDE);
    columnInclusionRule = configProperties
      .getInclusionRule(SC_COLUMN_PATTERN_INCLUDE, SC_COLUMN_PATTERN_EXCLUDE);

    routineInclusionRule = configProperties
      .getInclusionRuleDefaultExclude(SC_ROUTINE_PATTERN_INCLUDE,
                                      SC_ROUTINE_PATTERN_EXCLUDE);
    routineColumnInclusionRule = configProperties
      .getInclusionRule(SC_ROUTINE_COLUMN_PATTERN_INCLUDE,
                        SC_ROUTINE_COLUMN_PATTERN_EXCLUDE);

    grepColumnInclusionRule = Optional.ofNullable(configProperties
      .getInclusionRuleOrNull(SC_GREP_COLUMN_PATTERN_INCLUDE,
                              SC_GREP_COLUMN_PATTERN_EXCLUDE));
    grepRoutineColumnInclusionRule = Optional.ofNullable(configProperties
      .getInclusionRuleOrNull(SC_GREP_ROUTINE_COLUMN_PATTERN_INCLUDE,
                              SC_GREP_ROUTINE_COLUMN_PATTERN_EXCLUDE));
    grepDefinitionInclusionRule = Optional.ofNullable(configProperties
      .getInclusionRuleOrNull(SC_GREP_DEFINITION_PATTERN_INCLUDE,
                              SC_GREP_DEFINITION_PATTERN_EXCLUDE));

    return this;
  }

  @Override
  public SchemaCrawlerOptionsBuilder fromOptions(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      return this;
    }

    schemaInfoLevel = options.getSchemaInfoLevel();

    title = options.getTitle();

    schemaInclusionRule = options.getSchemaInclusionRule();
    synonymInclusionRule = options.getSynonymInclusionRule();
    sequenceInclusionRule = options.getSequenceInclusionRule();

    tableTypes = Optional.ofNullable(options.getTableTypes());
    tableNamePattern = options.getTableNamePattern();
    tableInclusionRule = options.getTableInclusionRule();
    columnInclusionRule = options.getColumnInclusionRule();

    routineTypes = Optional.ofNullable(options.getRoutineTypes());
    routineInclusionRule = options.getRoutineInclusionRule();
    routineColumnInclusionRule = options.getRoutineColumnInclusionRule();

    grepColumnInclusionRule = options.getGrepColumnInclusionRule();
    grepRoutineColumnInclusionRule = Optional
      .ofNullable(options.getGrepRoutineColumnInclusionRule()).orElse(null);
    grepDefinitionInclusionRule = Optional
      .ofNullable(options.getGrepDefinitionInclusionRule()).orElse(null);
    grepInvertMatch = options.isGrepInvertMatch();
    grepOnlyMatching = options.isGrepOnlyMatching();

    isNoEmptyTables = options.isNoEmptyTables();

    childTableFilterDepth = options.getChildTableFilterDepth();
    parentTableFilterDepth = options.getParentTableFilterDepth();

    return this;
  }

  public SchemaCrawlerOptionsBuilder grepOnlyMatching(final boolean grepOnlyMatching)
  {
    this.grepOnlyMatching = grepOnlyMatching;
    return this;
  }

  @Deprecated
  public final SchemaCrawlerOptionsBuilder hideEmptyTables()
  {
    return noEmptyTables(true);
  }

  @Deprecated
  public final SchemaCrawlerOptionsBuilder hideEmptyTables(final boolean value)
  {
    return noEmptyTables(value);
  }

  public SchemaCrawlerOptionsBuilder includeAllRoutines()
  {
    includeRoutines(new IncludeAll());
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeAllSequences()
  {
    includeSequences(new IncludeAll());
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeAllSynonyms()
  {
    includeSynonyms(new IncludeAll());
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeColumns(final InclusionRule columnInclusionRule)
  {
    if (columnInclusionRule == null)
    {
      this.columnInclusionRule = new IncludeAll();
    }
    else
    {
      this.columnInclusionRule = columnInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedColumns(final InclusionRule grepColumnInclusionRule)
  {
    this.grepColumnInclusionRule = Optional.ofNullable(grepColumnInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final InclusionRule grepDefinitionInclusionRule)
  {
    this.grepDefinitionInclusionRule = Optional
      .ofNullable(grepDefinitionInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedRoutineColumns(final InclusionRule grepRoutineColumnInclusionRule)
  {
    this.grepRoutineColumnInclusionRule = Optional
      .ofNullable(grepRoutineColumnInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutineColumns(final InclusionRule routineColumnInclusionRule)
  {
    if (routineColumnInclusionRule == null)
    {
      this.routineColumnInclusionRule = new IncludeAll();
    }
    else
    {
      this.routineColumnInclusionRule = routineColumnInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutines(final InclusionRule routineInclusionRule)
  {
    if (routineInclusionRule == null)
    {
      this.routineInclusionRule = new ExcludeAll();
      routineColumnInclusionRule = new ExcludeAll();
    }
    else
    {
      this.routineInclusionRule = routineInclusionRule;
      routineColumnInclusionRule = new IncludeAll();
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSchemas(final InclusionRule schemaInclusionRule)
  {
    if (schemaInclusionRule == null)
    {
      this.schemaInclusionRule = new IncludeAll();
    }
    else
    {
      this.schemaInclusionRule = schemaInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSequences(final InclusionRule sequenceInclusionRule)
  {
    if (sequenceInclusionRule == null)
    {
      this.sequenceInclusionRule = new ExcludeAll();
    }
    else
    {
      this.sequenceInclusionRule = sequenceInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSynonyms(final InclusionRule synonymInclusionRule)
  {
    if (synonymInclusionRule == null)
    {
      this.synonymInclusionRule = new ExcludeAll();
    }
    else
    {
      this.synonymInclusionRule = synonymInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeTables(final InclusionRule tableInclusionRule)
  {
    if (tableInclusionRule == null)
    {
      this.tableInclusionRule = new IncludeAll();
    }
    else
    {
      this.tableInclusionRule = tableInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder invertGrepMatch(final boolean grepInvertMatch)
  {
    this.grepInvertMatch = grepInvertMatch;
    return this;
  }

  /**
   * Corresponds to the -noemptytables command-line argument.
   */
  public final SchemaCrawlerOptionsBuilder noEmptyTables()
  {
    return noEmptyTables(true);
  }

  /**
   * Corresponds to the -noemptytables=&lt;boolean&gt; command-line
   * argument.
   */
  public final SchemaCrawlerOptionsBuilder noEmptyTables(final boolean value)
  {
    isNoEmptyTables = value;
    return this;
  }

  public SchemaCrawlerOptionsBuilder parentTableFilterDepth(final int parentTableFilterDepth)
  {
    if (parentTableFilterDepth < 0)
    {
      this.parentTableFilterDepth = 0;
    }
    else
    {
      this.parentTableFilterDepth = parentTableFilterDepth;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder routineTypes(final Collection<RoutineType> routineTypes)
  {
    if (routineTypes == null)
    {
      // null signifies include all routine types
      this.routineTypes = Optional.empty();
    }
    else if (routineTypes.isEmpty())
    {
      this.routineTypes = Optional.of(Collections.emptySet());
    }
    else
    {
      this.routineTypes = Optional.of(new HashSet<>(routineTypes));
    }
    return this;
  }

  /**
   * Sets routine types from a comma-separated list of routine types.
   *
   * @param routineTypesString
   *        Comma-separated list of routine types.
   */
  public SchemaCrawlerOptionsBuilder routineTypes(final String routineTypesString)
  {
    if (routineTypesString != null)
    {
      final Collection<RoutineType> routineTypes = new HashSet<>();
      final String[] routineTypeStrings = routineTypesString.split(",");
      if (routineTypeStrings != null && routineTypeStrings.length > 0)
      {
        for (final String routineTypeString: routineTypeStrings)
        {
          final RoutineType routineType = enumValue(routineTypeString
            .toLowerCase(Locale.ENGLISH), RoutineType.unknown);
          routineTypes.add(routineType);
        }
      }
      this.routineTypes = Optional.of(routineTypes);
    }
    else
    {
      routineTypes = Optional.empty();
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder tableNamePattern(final String tableNamePattern)
  {
    if (isBlank(tableNamePattern))
    {
      this.tableNamePattern = null;
    }
    else
    {
      this.tableNamePattern = tableNamePattern;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder tableTypes(final Collection<String> tableTypes)
  {
    if (tableTypes == null)
    {
      this.tableTypes = Optional.empty();
    }
    else if (tableTypes.isEmpty())
    {
      this.tableTypes = Optional.of(Collections.emptySet());
    }
    else
    {
      this.tableTypes = Optional.of(new HashSet<>(tableTypes));
    }
    return this;
  }

  /**
   * Sets table types requested for output from a comma-separated list
   * of table types. For example: TABLE,VIEW,SYSTEM_TABLE,GLOBAL
   * TEMPORARY,ALIAS,SYNONYM
   *
   * @param tableTypesString
   *        Comma-separated list of table types. Can be null if all
   *        supported table types are requested.
   */
  public SchemaCrawlerOptionsBuilder tableTypes(final String tableTypesString)
  {
    if (tableTypesString != null)
    {
      final Collection<String> tableTypes;
      tableTypes = new HashSet<>();
      final String[] tableTypeStrings = tableTypesString.split(",");
      if (tableTypeStrings != null && tableTypeStrings.length > 0)
      {
        for (final String tableTypeString: tableTypeStrings)
        {
          tableTypes.add(tableTypeString.trim());
        }
      }
      this.tableTypes = Optional.of(tableTypes);
    }
    else
    {
      tableTypes = Optional.empty();
    }

    return this;
  }

  public SchemaCrawlerOptionsBuilder title(final String title)
  {
    if (isBlank(title))
    {
      this.title = "";
    }
    else
    {
      this.title = title;
    }
    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaCrawlerOptions toOptions()
  {
    return new SchemaCrawlerOptions(schemaInfoLevel,
                                    title,
                                    schemaInclusionRule,
                                    synonymInclusionRule,
                                    sequenceInclusionRule,
                                    tableTypes.orElse(null),
                                    tableNamePattern,
                                    tableInclusionRule,
                                    columnInclusionRule,
                                    routineTypes.orElse(null),
                                    routineInclusionRule,
                                    routineColumnInclusionRule,
                                    grepColumnInclusionRule.orElse(null),
                                    grepRoutineColumnInclusionRule.orElse(null),
                                    grepDefinitionInclusionRule.orElse(null),
                                    grepInvertMatch,
                                    grepOnlyMatching,
                                    isNoEmptyTables,
                                    childTableFilterDepth,
                                    parentTableFilterDepth);
  }

  public SchemaCrawlerOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    if (schemaInfoLevel == null)
    {
      this.schemaInfoLevel = SchemaInfoLevelBuilder.standard().toOptions();
    }
    else
    {
      this.schemaInfoLevel = schemaInfoLevel;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevelBuilder schemaInfoLevelBuilder)
  {
    if (schemaInfoLevelBuilder == null)
    {
      schemaInfoLevel = SchemaInfoLevelBuilder.standard().toOptions();
    }
    else
    {
      schemaInfoLevel = schemaInfoLevelBuilder.toOptions();
    }
    return this;
  }

}
