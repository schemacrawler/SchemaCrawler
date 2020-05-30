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
the terms of the Eclipse @Deprecated public License v1.0, GNU General @Deprecated public License
v3 or GNU Lesser General @Deprecated public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse @Deprecated public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General @Deprecated public License v3 and the GNU Lesser General @Deprecated public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.schemacrawler;


import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Pattern;

import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
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

  private LimitOptionsBuilder limitOptionsBuilder;
  private FilterOptionsBuilder filterOptionsBuilder;
  private GrepOptionsBuilder grepOptionsBuilder;
  private LoadOptionsBuilder loadOptionsBuilder;

  /**
   * Default options.
   */
  private SchemaCrawlerOptionsBuilder()
  {
    limitOptionsBuilder = LimitOptionsBuilder.builder();
    filterOptionsBuilder = FilterOptionsBuilder.builder();
    grepOptionsBuilder = GrepOptionsBuilder.builder();
    loadOptionsBuilder = LoadOptionsBuilder.builder();
  }


  @Deprecated public SchemaCrawlerOptionsBuilder childTableFilterDepth(final int childTableFilterDepth)
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

    limitOptionsBuilder.fromConfig(config);
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

    limitOptionsBuilder = LimitOptionsBuilder.builder().fromOptions(options.getLimitOptions());
    filterOptionsBuilder = FilterOptionsBuilder.builder().fromOptions(options.getFilterOptions());
    grepOptionsBuilder = GrepOptionsBuilder.builder().fromOptions(options.getGrepOptions());
    loadOptionsBuilder = LoadOptionsBuilder.builder().fromOptions(options.getLoadOptions());

    return this;
  }

  @Override
  @Deprecated public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaCrawlerOptions toOptions()
  {
    final FilterOptions filterOptions = filterOptionsBuilder.toOptions();
    final GrepOptions grepOptions = grepOptionsBuilder.toOptions();
    final LoadOptions loadOptions = loadOptionsBuilder.toOptions();
    final LimitOptions limitOptions = limitOptionsBuilder.toOptions();

    return new SchemaCrawlerOptions(limitOptions,
                                    filterOptions,
                                    grepOptions,
                                    loadOptions);
  }


  @Deprecated public SchemaCrawlerOptionsBuilder grepOnlyMatching(final boolean grepOnlyMatching)
  {
    grepOptionsBuilder.grepOnlyMatching(grepOnlyMatching);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeAllRoutines()
  {
    includeRoutines(new IncludeAll());
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeAllSequences()
  {
    includeSequences(new IncludeAll());
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeAllSynonyms()
  {
    includeSynonyms(new IncludeAll());
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeColumns(final InclusionRule columnInclusionRule)
  {
    limitOptionsBuilder.includeColumns(columnInclusionRule);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeGreppedColumns(final InclusionRule grepColumnInclusionRule)
  {
    grepOptionsBuilder.includeGreppedColumns(grepColumnInclusionRule);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeSchemas(final Pattern schemaPattern)
  {
    limitOptionsBuilder.includeSchemas(schemaPattern);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeTables(final Pattern tablePattern)
  {
    limitOptionsBuilder.includeTables(tablePattern);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeRoutines(final Pattern routinePattern)
  {
    limitOptionsBuilder.includeRoutines(routinePattern);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeSequences(final Pattern sequencePattern)
  {
    limitOptionsBuilder.includeSequences(sequencePattern);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeSynonyms(final Pattern synonymPattern)
  {
    limitOptionsBuilder.includeSynonyms(synonymPattern);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeGreppedColumns(final Pattern grepColumnPattern)
  {
    grepOptionsBuilder.includeGreppedColumns(grepColumnPattern);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final InclusionRule grepDefinitionInclusionRule)
  {
    grepOptionsBuilder.includeGreppedDefinitions(grepDefinitionInclusionRule);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final Pattern grepDefinitionPattern)
  {
    grepOptionsBuilder.includeGreppedDefinitions(grepDefinitionPattern);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeGreppedRoutineParameters(final InclusionRule grepRoutineParameterInclusionRule)
  {
    grepOptionsBuilder.includeGreppedRoutineParameters(grepRoutineParameterInclusionRule);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder includeGreppedRoutineParameters(final Pattern grepRoutineParametersPattern)
  {
    grepOptionsBuilder.includeGreppedRoutineParameters(grepRoutineParametersPattern);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeRoutineParameters(final InclusionRule routineParameterInclusionRule)
  {
    limitOptionsBuilder.includeRoutineParameters(routineParameterInclusionRule);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeRoutines(final InclusionRule routineInclusionRule)
  {
    limitOptionsBuilder.includeRoutines(routineInclusionRule);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeSchemas(final InclusionRule schemaInclusionRule)
  {
    limitOptionsBuilder.includeSchemas(schemaInclusionRule);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeSequences(final InclusionRule sequenceInclusionRule)
  {
    limitOptionsBuilder.includeSequences(sequenceInclusionRule);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeSynonyms(final InclusionRule synonymInclusionRule)
  {
    limitOptionsBuilder.includeSynonyms(synonymInclusionRule);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder includeTables(final InclusionRule tableInclusionRule)
  {
    limitOptionsBuilder.includeTables(tableInclusionRule);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder invertGrepMatch(final boolean grepInvertMatch)
  {
    grepOptionsBuilder.invertGrepMatch(grepInvertMatch);
    return this;
  }

  /**
   * Corresponds to the --no-empty-tables command-line argument.
   *
   */

  @Deprecated public final SchemaCrawlerOptionsBuilder noEmptyTables()
  {
    return noEmptyTables(true);
  }

  /**
   * Corresponds to the --no-empty-tables=&lt;boolean&gt; command-line argument.
   */

  @Deprecated public final SchemaCrawlerOptionsBuilder noEmptyTables(final boolean value)
  {
    filterOptionsBuilder.noEmptyTables(value);
    return this;
  }

  /**
   * Corresponds to the --load-row-counts command-line argument.
   *
   */

  @Deprecated public final SchemaCrawlerOptionsBuilder loadRowCounts()
  {
    loadOptionsBuilder.loadRowCounts(true);
    return this;
  }

  /**
   * Corresponds to the --load-row-counts=&lt;boolean&gt; command-line argument.
   *
   */

  @Deprecated public final SchemaCrawlerOptionsBuilder loadRowCounts(final boolean value)
  {
    loadOptionsBuilder.loadRowCounts(value);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder parentTableFilterDepth(final int parentTableFilterDepth)
  {
    filterOptionsBuilder.parentTableFilterDepth(parentTableFilterDepth);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder routineTypes(final Collection<RoutineType> routineTypes)
  {
    limitOptionsBuilder.routineTypes(routineTypes);
    return this;
  }

  /**
   * Sets routine types from a comma-separated list of routine types.
   *
   * @param routineTypesString
   *   Comma-separated list of routine types.
   */
  @Deprecated public SchemaCrawlerOptionsBuilder routineTypes(final String routineTypesString)
  {
    limitOptionsBuilder.routineTypes(routineTypesString);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder tableNamePattern(final String tableNamePattern)
  {
    limitOptionsBuilder.tableNamePattern(tableNamePattern);
    return this;
  }

  @Deprecated public SchemaCrawlerOptionsBuilder tableTypes(final Collection<String> tableTypes)
  {
    limitOptionsBuilder.tableTypes(tableTypes);
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
  @Deprecated public SchemaCrawlerOptionsBuilder tableTypes(final String tableTypesString)
  {
    limitOptionsBuilder.tableTypes(tableTypesString);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    loadOptionsBuilder.withSchemaInfoLevel(schemaInfoLevel);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder withSchemaInfoLevelBuilder(final SchemaInfoLevelBuilder schemaInfoLevelBuilder)
  {
    loadOptionsBuilder.withSchemaInfoLevelBuilder(schemaInfoLevelBuilder);
    return this;
  }


  @Deprecated public SchemaCrawlerOptionsBuilder withInfoLevel(final InfoLevel infoLevel)
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

  public SchemaCrawlerOptionsBuilder withLimitOptions(final LimitOptions limitOptions)
  {
    if (limitOptions != null)
    {
      this.limitOptionsBuilder = LimitOptionsBuilder.builder().fromOptions(limitOptions);
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLimitOptionsBuilder(final LimitOptionsBuilder limitOptionsBuilder)
  {
    if (limitOptionsBuilder != null)
    {
      this.limitOptionsBuilder = limitOptionsBuilder;
    }
    return this;
  }

  public LimitOptions getLimitOptions()
  {
    return limitOptionsBuilder.toOptions();
  }

  public FilterOptions getFilterOptions()
  {
    return filterOptionsBuilder.toOptions();
  }

  public GrepOptions getGrepOptions()
  {
    return grepOptionsBuilder.toOptions();
  }

  public LoadOptions getLoadOptions()
  {
    return loadOptionsBuilder.toOptions();
  }
}
