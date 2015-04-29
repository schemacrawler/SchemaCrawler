/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

package schemacrawler.schemacrawler;


import java.util.Collection;

import schemacrawler.schema.RoutineType;

/**
 * SchemaCrawler options.
 *
 * @author Sualeh Fatehi
 */
public class SchemaCrawlerOptionsBuilder
  implements OptionsBuilder<SchemaCrawlerOptions>
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

  private static final String SC_GREP_INVERT_MATCH = "schemacrawler.grep.invert-match";
  private static final String SC_GREP_ONLY_MATCHING = "schemacrawler.grep.only-matching";
  private static final String SC_HIDE_EMPTY_TABLES = "schemacrawler.hide.empty-tables";
  private final SchemaCrawlerOptions options;

  public SchemaCrawlerOptionsBuilder()
  {
    this(new SchemaCrawlerOptions());
  }

  public SchemaCrawlerOptionsBuilder(final SchemaCrawlerOptions options)
  {
    this.options = options;
  }

  public SchemaCrawlerOptionsBuilder childTableFilterDepth(final int childTableFilterDepth)
  {
    options.setChildTableFilterDepth(childTableFilterDepth);
    return this;
  }

  public InclusionRule getColumnInclusionRule()
  {
    return options.getColumnInclusionRule();
  }

  public InclusionRule getGrepColumnInclusionRule()
  {
    return options.getGrepColumnInclusionRule();
  }

  public InclusionRule getGrepDefinitionInclusionRule()
  {
    return options.getGrepDefinitionInclusionRule();
  }

  public InclusionRule getGrepRoutineColumnInclusionRule()
  {
    return options.getGrepRoutineColumnInclusionRule();
  }

  public InclusionRule getRoutineColumnInclusionRule()
  {
    return options.getRoutineColumnInclusionRule();
  }

  public InclusionRule getRoutineInclusionRule()
  {
    return options.getRoutineInclusionRule();
  }

  public InclusionRule getSchemaInclusionRule()
  {
    return options.getSchemaInclusionRule();
  }

  public InclusionRule getSequenceInclusionRule()
  {
    return options.getSequenceInclusionRule();
  }

  public InclusionRule getSynonymInclusionRule()
  {
    return options.getSynonymInclusionRule();
  }

  public InclusionRule getTableInclusionRule()
  {
    return options.getTableInclusionRule();
  }

  public SchemaCrawlerOptionsBuilder hideEmptyTables()
  {
    options.setHideEmptyTables(true);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeColumns(final InclusionRule columnInclusionRule)
  {
    options.setColumnInclusionRule(columnInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedColumns(final InclusionRule grepColumnInclusionRule)
  {
    options.setGrepColumnInclusionRule(grepColumnInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeGreppedDefinitions(final InclusionRule grepDefinitionInclusionRule)
  {
    options.setGrepDefinitionInclusionRule(grepDefinitionInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutineColumns(final InclusionRule routineColumnInclusionRule)
  {
    options.setRoutineColumnInclusionRule(routineColumnInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeRoutines(final InclusionRule routineInclusionRule)
  {
    options.setRoutineInclusionRule(routineInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSchemas(final InclusionRule schemaInclusionRule)
  {
    options.setSchemaInclusionRule(schemaInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSequences(final InclusionRule sequenceInclusionRule)
  {
    options.setSequenceInclusionRule(sequenceInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeSynonyms(final InclusionRule synonymInclusionRule)
  {
    options.setSynonymInclusionRule(synonymInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder includeTables(final InclusionRule tableInclusionRule)
  {
    options.setTableInclusionRule(tableInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder parentTableFilterDepth(final int parentTableFilterDepth)
  {
    options.setParentTableFilterDepth(parentTableFilterDepth);
    return this;
  }

  public SchemaCrawlerOptionsBuilder routineTypes(final Collection<RoutineType> routineTypes)
  {
    options.setRoutineTypes(routineTypes);
    return this;
  }

  public SchemaCrawlerOptionsBuilder routineTypes(final String routineTypesString)
  {
    options.setRoutineTypes(routineTypesString);
    return this;
  }

  public SchemaCrawlerOptionsBuilder schemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    options.setSchemaInfoLevel(schemaInfoLevel);
    return this;
  }

  public SchemaCrawlerOptionsBuilder setDatabaseSpecificOverrideOptions(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
  {
    options.setDatabaseSpecificOverrideOptions(databaseSpecificOverrideOptions);
    return this;
  }

  /**
   * Options from properties.
   *
   * @param config
   *        Configuration properties
   */
  @Override
  public SchemaCrawlerOptionsBuilder setFromConfig(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    options.setInformationSchemaViews(new InformationSchemaViews(config));
    options
      .setDatabaseSpecificOverrideOptions(new DatabaseSpecificOverrideOptions(config));

    options.setSchemaInclusionRule(configProperties
      .getInclusionRule(SC_SCHEMA_PATTERN_INCLUDE, SC_SCHEMA_PATTERN_EXCLUDE));
    options.setSynonymInclusionRule(configProperties
      .getInclusionRuleDefaultExclude(SC_SYNONYM_PATTERN_INCLUDE,
                                      SC_SYNONYM_PATTERN_EXCLUDE));
    options.setSequenceInclusionRule(configProperties
      .getInclusionRuleDefaultExclude(SC_SEQUENCE_PATTERN_INCLUDE,
                                      SC_SEQUENCE_PATTERN_EXCLUDE));

    options.setTableInclusionRule(configProperties
      .getInclusionRule(SC_TABLE_PATTERN_INCLUDE, SC_TABLE_PATTERN_EXCLUDE));
    options.setColumnInclusionRule(configProperties
      .getInclusionRule(SC_COLUMN_PATTERN_INCLUDE, SC_COLUMN_PATTERN_EXCLUDE));

    options
      .setRoutineInclusionRule(configProperties
        .getInclusionRule(SC_ROUTINE_PATTERN_INCLUDE,
                          SC_ROUTINE_PATTERN_EXCLUDE));
    options.setRoutineColumnInclusionRule(configProperties
      .getInclusionRule(SC_ROUTINE_COLUMN_PATTERN_INCLUDE,
                        SC_ROUTINE_COLUMN_PATTERN_EXCLUDE));

    options.setGrepColumnInclusionRule(configProperties
      .getInclusionRuleOrNull(SC_GREP_COLUMN_PATTERN_INCLUDE,
                              SC_GREP_COLUMN_PATTERN_EXCLUDE));
    options.setGrepRoutineColumnInclusionRule(configProperties
      .getInclusionRuleOrNull(SC_GREP_ROUTINE_COLUMN_PATTERN_INCLUDE,
                              SC_GREP_ROUTINE_COLUMN_PATTERN_EXCLUDE));
    options.setGrepDefinitionInclusionRule(configProperties
      .getInclusionRuleOrNull(SC_GREP_DEFINITION_PATTERN_INCLUDE,
                              SC_GREP_DEFINITION_PATTERN_EXCLUDE));

    options.setGrepInvertMatch(configProperties
      .getBooleanValue(SC_GREP_INVERT_MATCH));
    options.setGrepOnlyMatching(configProperties
      .getBooleanValue(SC_GREP_ONLY_MATCHING));

    options.setHideEmptyTables(configProperties
      .getBooleanValue(SC_HIDE_EMPTY_TABLES));

    return this;
  }

  public SchemaCrawlerOptionsBuilder setGrepInvertMatch(final boolean grepInvertMatch)
  {
    options.setGrepInvertMatch(grepInvertMatch);
    return this;
  }

  public SchemaCrawlerOptionsBuilder setGrepOnlyMatching(final boolean grepOnlyMatching)
  {
    options.setGrepOnlyMatching(grepOnlyMatching);
    return this;
  }

  public SchemaCrawlerOptionsBuilder setGrepRoutineColumnInclusionRule(final InclusionRule grepRoutineColumnInclusionRule)
  {
    options.setGrepRoutineColumnInclusionRule(grepRoutineColumnInclusionRule);
    return this;
  }

  public SchemaCrawlerOptionsBuilder setInformationSchemaViews(final InformationSchemaViews informationSchemaViews)
  {
    options.setInformationSchemaViews(informationSchemaViews);
    return this;
  }

  public SchemaCrawlerOptionsBuilder tableNamePattern(final String tableNamePattern)
  {
    options.setTableNamePattern(tableNamePattern);
    return this;
  }

  public SchemaCrawlerOptionsBuilder tableTypes(final Collection<String> tableTypes)
  {
    options.setTableTypes(tableTypes);
    return this;
  }

  public SchemaCrawlerOptionsBuilder tableTypes(final String tableTypesString)
  {
    options.setTableTypesFromString(tableTypesString);
    return this;
  }

  public SchemaCrawlerOptionsBuilder title(final String title)
  {
    options.setTitle(title);
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
    return options;
  }

}
