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

import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.RoutineType;

/**
 * SchemaCrawler options builder, to build the immutable options to crawl a schema.
 */
public final class SchemaCrawlerOptionsBuilder
  implements OptionsBuilder<SchemaCrawlerOptionsBuilder, SchemaCrawlerOptions>
{

  private static final String SC_COLUMN_PATTERN_EXCLUDE =
    "schemacrawler.column.pattern.exclude";
  private static final String SC_COLUMN_PATTERN_INCLUDE =
    "schemacrawler.column.pattern.include";
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

  private InclusionRule columnInclusionRule;
  private InclusionRule routineInclusionRule;
  private InclusionRule routineParameterInclusionRule;
  private Optional<Collection<RoutineType>> routineTypes;
  private InclusionRule schemaInclusionRule;
  private InclusionRule sequenceInclusionRule;
  private InclusionRule synonymInclusionRule;
  private InclusionRule tableInclusionRule;
  private String tableNamePattern;
  private Optional<Collection<String>> tableTypes;
  private FilterOptionsBuilder filterOptionsBuilder;
  private GrepOptionsBuilder grepOptionsBuilder;
  private LoadOptionsBuilder loadOptionsBuilder;

  /**
   * Default options.
   */
  private SchemaCrawlerOptionsBuilder()
  {
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

    filterOptionsBuilder = FilterOptionsBuilder.builder();
    grepOptionsBuilder = GrepOptionsBuilder.builder();
    loadOptionsBuilder = LoadOptionsBuilder.builder();
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder childTableFilterDepth(final int childTableFilterDepth)
  {
    filterOptionsBuilder.childTableFilterDepth(childTableFilterDepth);
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

    grepOptionsBuilder.fromConfig(config);
    loadOptionsBuilder.fromConfig(config);

    return this;
  }

  @Override
  public SchemaCrawlerOptionsBuilder fromOptions(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      return this;
    }

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

    filterOptionsBuilder = FilterOptionsBuilder.builder().fromOptions(options.getFilterOptions());
    grepOptionsBuilder = GrepOptionsBuilder.builder().fromOptions(options.getGrepOptions());
    loadOptionsBuilder = LoadOptionsBuilder.builder().fromOptions(options.getLoadOptions());

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
    final FilterOptions filterOptions = filterOptionsBuilder.toOptions();
    final GrepOptions grepOptions = grepOptionsBuilder.toOptions();
    final LoadOptions loadOptions = loadOptionsBuilder.toOptions();

    return new SchemaCrawlerOptions(schemaInclusionRule,
                                    synonymInclusionRule,
                                    sequenceInclusionRule,
                                    tableTypes.orElse(null),
                                    tableNamePattern,
                                    tableInclusionRule,
                                    columnInclusionRule,
                                    routineTypes.orElse(null),
                                    routineInclusionRule,
                                    routineParameterInclusionRule,
                                    filterOptions,
                                    grepOptions,
                                    loadOptions);
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder grepOnlyMatching(final boolean grepOnlyMatching)
  {
    grepOptionsBuilder.grepOnlyMatching(grepOnlyMatching);
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

  @Deprecated
  public SchemaCrawlerOptionsBuilder includeGreppedColumns(final InclusionRule grepColumnInclusionRule)
  {
    grepOptionsBuilder.includeGreppedColumns(grepColumnInclusionRule);
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

  @Deprecated
  public SchemaCrawlerOptionsBuilder includeGreppedColumns(final Pattern grepColumnPattern)
  {
    grepOptionsBuilder.includeGreppedColumns(grepColumnPattern);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final InclusionRule grepDefinitionInclusionRule)
  {
    grepOptionsBuilder.includeGreppedDefinitions(grepDefinitionInclusionRule);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final Pattern grepDefinitionPattern)
  {
    grepOptionsBuilder.includeGreppedDefinitions(grepDefinitionPattern);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder includeGreppedRoutineParameters(final InclusionRule grepRoutineParameterInclusionRule)
  {
    grepOptionsBuilder.includeGreppedRoutineParameters(grepRoutineParameterInclusionRule);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder includeGreppedRoutineParameters(final Pattern grepRoutineParametersPattern)
  {
    grepOptionsBuilder.includeGreppedRoutineParameters(grepRoutineParametersPattern);
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

  @Deprecated
  public SchemaCrawlerOptionsBuilder invertGrepMatch(final boolean grepInvertMatch)
  {
    grepOptionsBuilder.invertGrepMatch(grepInvertMatch);
    return this;
  }

  /**
   * Corresponds to the --no-empty-tables command-line argument.
   * @deprecated
   */
  @Deprecated
  public final SchemaCrawlerOptionsBuilder noEmptyTables()
  {
    return noEmptyTables(true);
  }

  /**
   * Corresponds to the --no-empty-tables=&lt;boolean&gt; command-line argument.
   */
  @Deprecated
  public final SchemaCrawlerOptionsBuilder noEmptyTables(final boolean value)
  {
    filterOptionsBuilder.noEmptyTables(value);
    return this;
  }

  /**
   * Corresponds to the --load-row-counts command-line argument.
   * @deprecated
   */
  @Deprecated
  public final SchemaCrawlerOptionsBuilder loadRowCounts()
  {
    loadOptionsBuilder.loadRowCounts(true);
    return this;
  }

  /**
   * Corresponds to the --load-row-counts=&lt;boolean&gt; command-line argument.
   * @deprecated
   */
  @Deprecated
  public final SchemaCrawlerOptionsBuilder loadRowCounts(final boolean value)
  {
    loadOptionsBuilder.loadRowCounts(value);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder parentTableFilterDepth(final int parentTableFilterDepth)
  {
    filterOptionsBuilder.parentTableFilterDepth(parentTableFilterDepth);
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

  @Deprecated
  public SchemaCrawlerOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    loadOptionsBuilder.withSchemaInfoLevel(schemaInfoLevel);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder withSchemaInfoLevelBuilder(final SchemaInfoLevelBuilder schemaInfoLevelBuilder)
  {
    loadOptionsBuilder.withSchemaInfoLevelBuilder(schemaInfoLevelBuilder);
    return this;
  }

  @Deprecated
  public SchemaCrawlerOptionsBuilder withInfoLevel(final InfoLevel infoLevel)
  {
    loadOptionsBuilder.withInfoLevel(infoLevel);
    return this;
  }

  public SchemaCrawlerOptionsBuilder withGrepOptions(final GrepOptions grepOptions)
  {
    if (grepOptions != null)
    {
      this.grepOptionsBuilder = GrepOptionsBuilder.builder().fromOptions(grepOptions);
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withGrepOptionsBuilder(final GrepOptionsBuilder grepOptionsBuilder)
  {
    if (grepOptionsBuilder != null)
    {
      this.grepOptionsBuilder = grepOptionsBuilder;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLoadOptions(final LoadOptions loadOptions)
  {
    if (loadOptions != null)
    {
      this.loadOptionsBuilder = LoadOptionsBuilder.builder().fromOptions(loadOptions);
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLoadOptionsBuilder(final LoadOptionsBuilder loadOptionsBuilder)
  {
    if (loadOptionsBuilder != null)
    {
      this.loadOptionsBuilder = loadOptionsBuilder;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withFilterOptions(final FilterOptions filterOptions)
  {
    if (filterOptions != null)
    {
      this.filterOptionsBuilder = FilterOptionsBuilder.builder().fromOptions(filterOptions);
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withFilterOptionsBuilder(final FilterOptionsBuilder filterOptionsBuilder)
  {
    if (filterOptionsBuilder != null)
    {
      this.filterOptionsBuilder = filterOptionsBuilder;
    }
    return this;
  }

}
