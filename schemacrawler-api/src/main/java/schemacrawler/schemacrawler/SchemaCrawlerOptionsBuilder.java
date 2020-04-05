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
import java.util.regex.Pattern;

import schemacrawler.schema.RoutineType;

/**
 * SchemaCrawler options.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptionsBuilder
  implements OptionsBuilder<SchemaCrawlerOptionsBuilder, SchemaCrawlerOptions>
{

  private static final String SC_COLUMN_PATTERN_EXCLUDE =
    "schemacrawler.column.pattern.exclude";
  private static final String SC_COLUMN_PATTERN_INCLUDE =
    "schemacrawler.column.pattern.include";
  private static final String SC_GREP_COLUMN_PATTERN_EXCLUDE =
    "schemacrawler.grep.column.pattern.exclude";
  private static final String SC_GREP_COLUMN_PATTERN_INCLUDE =
    "schemacrawler.grep.column.pattern.include";
  private static final String SC_GREP_DEFINITION_PATTERN_EXCLUDE =
    "schemacrawler.grep.definition.pattern.exclude";
  private static final String SC_GREP_DEFINITION_PATTERN_INCLUDE =
    "schemacrawler.grep.definition.pattern.include";
  private static final String SC_GREP_ROUTINE_PARAMETER_PATTERN_EXCLUDE =
    "schemacrawler.grep.routine.inout.pattern.exclude";
  private static final String SC_GREP_ROUTINE_PARAMETER_PATTERN_INCLUDE =
    "schemacrawler.grep.routine.inout.pattern.include";
  private static final String SC_ROUTINE_PARAMETER_PATTERN_EXCLUDE =
    "schemacrawler.routine.inout.pattern.exclude";
  private static final String SC_ROUTINE_PARAMETER_PATTERN_INCLUDE =
    "schemacrawler.routine.inout.pattern.include";
  private static final String SC_ROUTINE_PATTERN_EXCLUDE =
    "schemacrawler.routine.pattern.exclude";
  private static final String SC_ROUTINE_PATTERN_INCLUDE =
    "schemacrawler.routine.pattern.include";
  private static final String SC_SCHEMA_PATTERN_EXCLUDE =
    "schemacrawler.schema.pattern.exclude";
  private static final String SC_SCHEMA_PATTERN_INCLUDE =
    "schemacrawler.schema.pattern.include";
  private static final String SC_SEQUENCE_PATTERN_EXCLUDE =
    "schemacrawler.sequence.pattern.exclude";
  private static final String SC_SEQUENCE_PATTERN_INCLUDE =
    "schemacrawler.sequence.pattern.include";
  private static final String SC_SYNONYM_PATTERN_EXCLUDE =
    "schemacrawler.synonym.pattern.exclude";
  private static final String SC_SYNONYM_PATTERN_INCLUDE =
    "schemacrawler.synonym.pattern.include";
  private static final String SC_TABLE_PATTERN_EXCLUDE =
    "schemacrawler.table.pattern.exclude";
  private static final String SC_TABLE_PATTERN_INCLUDE =
    "schemacrawler.table.pattern.include";

  private static Collection<RoutineType> allRoutineTypes()
  {
    return EnumSet.of(RoutineType.procedure, RoutineType.function);
  }

  public static SchemaCrawlerOptionsBuilder builder()
  {
    return new SchemaCrawlerOptionsBuilder();
  }

  private static Collection<String> defaultTableTypes()
  {
    return Arrays.asList("BASE TABLE", "TABLE", "VIEW");
  }

  public static SchemaCrawlerOptions newSchemaCrawlerOptions()
  {
    return builder().toOptions();
  }

  private int childTableFilterDepth;
  private InclusionRule columnInclusionRule;
  private Optional<InclusionRule> grepColumnInclusionRule;
  private Optional<InclusionRule> grepDefinitionInclusionRule;
  private boolean grepInvertMatch;
  private boolean grepOnlyMatching;
  private Optional<InclusionRule> grepRoutineParameterInclusionRule;
  private boolean isNoEmptyTables;
  private boolean isLoadRowCounts;
  private int parentTableFilterDepth;
  private InclusionRule routineInclusionRule;

  private InclusionRule routineParameterInclusionRule;
  private Optional<Collection<RoutineType>> routineTypes;
  private InclusionRule schemaInclusionRule;
  private SchemaInfoLevelBuilder schemaInfoLevelBuilder;
  private InclusionRule sequenceInclusionRule;
  private InclusionRule synonymInclusionRule;
  private InclusionRule tableInclusionRule;
  private String tableNamePattern;
  private Optional<Collection<String>> tableTypes;

  /**
   * Default options.
   */
  private SchemaCrawlerOptionsBuilder()
  {
    schemaInfoLevelBuilder = SchemaInfoLevelBuilder
      .builder()
      .withInfoLevel(InfoLevel.standard);

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
    routineParameterInclusionRule = new ExcludeAll();

    grepColumnInclusionRule = Optional.empty();
    grepRoutineParameterInclusionRule = Optional.empty();
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
   *   Configuration properties
   */
  @Override
  public SchemaCrawlerOptionsBuilder fromConfig(final Config config)
  {
    if (config == null)
    {
      return this;
    }

    schemaInfoLevelBuilder = SchemaInfoLevelBuilder
      .builder()
      .fromConfig(config);

    schemaInclusionRule = config.getInclusionRuleWithDefault(
      SC_SCHEMA_PATTERN_INCLUDE,
      SC_SCHEMA_PATTERN_EXCLUDE,
      IncludeAll::new);
    synonymInclusionRule = config.getInclusionRuleWithDefault(
      SC_SYNONYM_PATTERN_INCLUDE,
      SC_SYNONYM_PATTERN_EXCLUDE,
      ExcludeAll::new);
    sequenceInclusionRule = config.getInclusionRuleWithDefault(
      SC_SEQUENCE_PATTERN_INCLUDE,
      SC_SEQUENCE_PATTERN_EXCLUDE,
      ExcludeAll::new);

    tableInclusionRule = config.getInclusionRuleWithDefault(
      SC_TABLE_PATTERN_INCLUDE,
      SC_TABLE_PATTERN_EXCLUDE,
      IncludeAll::new);
    columnInclusionRule = config.getInclusionRuleWithDefault(
      SC_COLUMN_PATTERN_INCLUDE,
      SC_COLUMN_PATTERN_EXCLUDE,
      IncludeAll::new);

    routineInclusionRule = config.getInclusionRuleWithDefault(
      SC_ROUTINE_PATTERN_INCLUDE,
      SC_ROUTINE_PATTERN_EXCLUDE,
      ExcludeAll::new);
    routineParameterInclusionRule = config.getInclusionRuleWithDefault(
      SC_ROUTINE_PARAMETER_PATTERN_INCLUDE,
      SC_ROUTINE_PARAMETER_PATTERN_EXCLUDE,
      IncludeAll::new);

    grepColumnInclusionRule = config.getOptionalInclusionRule(
      SC_GREP_COLUMN_PATTERN_INCLUDE,
      SC_GREP_COLUMN_PATTERN_EXCLUDE);
    grepRoutineParameterInclusionRule = config.getOptionalInclusionRule(
      SC_GREP_ROUTINE_PARAMETER_PATTERN_INCLUDE,
      SC_GREP_ROUTINE_PARAMETER_PATTERN_EXCLUDE);
    grepDefinitionInclusionRule = config.getOptionalInclusionRule(
      SC_GREP_DEFINITION_PATTERN_INCLUDE,
      SC_GREP_DEFINITION_PATTERN_EXCLUDE);

    return this;
  }

  @Override
  public SchemaCrawlerOptionsBuilder fromOptions(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      return this;
    }

    schemaInfoLevelBuilder = SchemaInfoLevelBuilder
      .builder()
      .fromOptions(options.getSchemaInfoLevel());

    schemaInclusionRule = options.getSchemaInclusionRule();
    synonymInclusionRule = options.getSynonymInclusionRule();
    sequenceInclusionRule = options.getSequenceInclusionRule();

    tableTypes = Optional.ofNullable(options.getTableTypes());
    tableNamePattern = options.getTableNamePattern();
    tableInclusionRule = options.getTableInclusionRule();
    columnInclusionRule = options.getColumnInclusionRule();

    routineTypes = Optional.ofNullable(options.getRoutineTypes());
    routineInclusionRule = options.getRoutineInclusionRule();
    routineParameterInclusionRule = options.getRoutineParameterInclusionRule();

    grepColumnInclusionRule = options.getGrepColumnInclusionRule();
    grepRoutineParameterInclusionRule = Optional
      .ofNullable(options.getGrepRoutineParameterInclusionRule())
      .orElse(null);
    grepDefinitionInclusionRule = Optional
      .ofNullable(options.getGrepDefinitionInclusionRule())
      .orElse(null);
    grepInvertMatch = options.isGrepInvertMatch();
    grepOnlyMatching = options.isGrepOnlyMatching();

    isNoEmptyTables = options.isNoEmptyTables();
    isLoadRowCounts = options.isLoadRowCounts();

    childTableFilterDepth = options.getChildTableFilterDepth();
    parentTableFilterDepth = options.getParentTableFilterDepth();

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
    if (tableInclusionRule instanceof ExcludeAll)
    {
      schemaInfoLevelBuilder.withoutTables();
    }
    if (routineInclusionRule instanceof ExcludeAll)
    {
      schemaInfoLevelBuilder.withoutRoutines();
    }

    return new SchemaCrawlerOptions(schemaInfoLevelBuilder.toOptions(),
                                    schemaInclusionRule,
                                    synonymInclusionRule,
                                    sequenceInclusionRule,
                                    tableTypes.orElse(null),
                                    tableNamePattern,
                                    tableInclusionRule,
                                    columnInclusionRule,
                                    routineTypes.orElse(null),
                                    routineInclusionRule,
                                    routineParameterInclusionRule,
                                    grepColumnInclusionRule.orElse(null),
                                    grepRoutineParameterInclusionRule.orElse(
                                      null),
                                    grepDefinitionInclusionRule.orElse(null),
                                    grepInvertMatch,
                                    grepOnlyMatching,
                                    isNoEmptyTables,
                                    isLoadRowCounts,
                                    childTableFilterDepth,
                                    parentTableFilterDepth);
  }

  public SchemaCrawlerOptionsBuilder grepOnlyMatching(final boolean grepOnlyMatching)
  {
    this.grepOnlyMatching = grepOnlyMatching;
    return this;
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

  public SchemaCrawlerOptionsBuilder includeSchemas(final Pattern schemaPattern)
  {
    schemaInclusionRule = new RegularExpressionInclusionRule(schemaPattern);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeTables(final Pattern tablePattern)
  {
    tableInclusionRule = new RegularExpressionInclusionRule(tablePattern);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutines(final Pattern routinePattern)
  {
    routineInclusionRule = new RegularExpressionInclusionRule(routinePattern);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSequences(final Pattern sequencePattern)
  {
    sequenceInclusionRule = new RegularExpressionInclusionRule(sequencePattern);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSynonyms(final Pattern synonymPattern)
  {
    synonymInclusionRule = new RegularExpressionInclusionRule(synonymPattern);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedColumns(final Pattern grepColumnPattern)
  {
    if (grepColumnPattern == null)
    {
      grepColumnInclusionRule = Optional.empty();
    }
    else
    {
      grepColumnInclusionRule =
        Optional.of(new RegularExpressionInclusionRule(grepColumnPattern));
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final InclusionRule grepDefinitionInclusionRule)
  {
    this.grepDefinitionInclusionRule =
      Optional.ofNullable(grepDefinitionInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final Pattern grepDefinitionPattern)
  {
    if (grepDefinitionPattern == null)
    {
      grepDefinitionInclusionRule = Optional.empty();
    }
    else
    {
      grepDefinitionInclusionRule =
        Optional.of(new RegularExpressionInclusionRule(grepDefinitionPattern));
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedRoutineParameters(final InclusionRule grepRoutineParameterInclusionRule)
  {
    this.grepRoutineParameterInclusionRule =
      Optional.ofNullable(grepRoutineParameterInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedRoutineParameters(final Pattern grepRoutineParametersPattern)
  {
    if (grepRoutineParametersPattern == null)
    {
      grepRoutineParameterInclusionRule = Optional.empty();
    }
    else
    {
      grepRoutineParameterInclusionRule =
        Optional.of(new RegularExpressionInclusionRule(
          grepRoutineParametersPattern));
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutineParameters(final InclusionRule routineParameterInclusionRule)
  {
    if (routineParameterInclusionRule == null)
    {
      this.routineParameterInclusionRule = new IncludeAll();
    }
    else
    {
      this.routineParameterInclusionRule = routineParameterInclusionRule;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutines(final InclusionRule routineInclusionRule)
  {
    if (routineInclusionRule == null)
    {
      this.routineInclusionRule = new ExcludeAll();
      routineParameterInclusionRule = new ExcludeAll();
    }
    else
    {
      this.routineInclusionRule = routineInclusionRule;
      routineParameterInclusionRule = new IncludeAll();
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
   * Corresponds to the --no-empty-tables command-line argument.
   */
  public final SchemaCrawlerOptionsBuilder noEmptyTables()
  {
    return noEmptyTables(true);
  }

  /**
   * Corresponds to the --no-empty-tables=&lt;boolean&gt; command-line argument.
   */
  public final SchemaCrawlerOptionsBuilder noEmptyTables(final boolean value)
  {
    isNoEmptyTables = value;
    return this;
  }

  /**
   * Corresponds to the --load-row-counts command-line argument.
   */
  public final SchemaCrawlerOptionsBuilder loadRowCounts()
  {
    return loadRowCounts(true);
  }

  /**
   * Corresponds to the --load-row-counts=&lt;boolean&gt; command-line argument.
   */
  public final SchemaCrawlerOptionsBuilder loadRowCounts(final boolean value)
  {
    isLoadRowCounts = value;
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
   *   Comma-separated list of routine types.
   */
  public SchemaCrawlerOptionsBuilder routineTypes(final String routineTypesString)
  {
    if (routineTypesString != null)
    {
      final Collection<RoutineType> routineTypes = new HashSet<>();
      final String[] routineTypeStrings = routineTypesString.split(",");
      if (routineTypeStrings != null && routineTypeStrings.length > 0)
      {
        for (final String routineTypeString : routineTypeStrings)
        {
          final RoutineType routineType =
            enumValue(routineTypeString.toLowerCase(Locale.ENGLISH),
                      RoutineType.unknown);
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
   * Sets table types requested for output from a comma-separated list of table
   * types. For example: TABLE,VIEW,SYSTEM_TABLE,GLOBAL TEMPORARY,ALIAS,SYNONYM
   *
   * @param tableTypesString
   *   Comma-separated list of table types. Can be null if all supported table
   *   types are requested.
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
        for (final String tableTypeString : tableTypeStrings)
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

  public SchemaCrawlerOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    if (schemaInfoLevel != null)
    {
      schemaInfoLevelBuilder = SchemaInfoLevelBuilder
        .builder()
        .fromOptions(schemaInfoLevel);
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevelBuilder schemaInfoLevelBuilder)
  {
    if (schemaInfoLevelBuilder == null)
    {
      this.schemaInfoLevelBuilder = SchemaInfoLevelBuilder
        .builder()
        .withInfoLevel(InfoLevel.standard);
    }
    else
    {
      this.schemaInfoLevelBuilder = schemaInfoLevelBuilder;
    }
    return this;
  }

}
