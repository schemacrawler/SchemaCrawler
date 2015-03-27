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


import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import schemacrawler.schema.RoutineType;
import sf.util.ObjectToString;

/**
 * SchemaCrawler options.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptions
  implements Options
{

  private static final long serialVersionUID = -3557794862382066029L;

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

  private SchemaInfoLevel schemaInfoLevel;

  private InformationSchemaViews informationSchemaViews;
  private DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions;

  private InclusionRule schemaInclusionRule;
  private InclusionRule synonymInclusionRule;
  private InclusionRule sequenceInclusionRule;

  private Collection<String> tableTypes;
  private String tableNamePattern;
  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;

  private Collection<RoutineType> routineTypes;
  private InclusionRule routineInclusionRule;
  private InclusionRule routineColumnInclusionRule;

  private InclusionRule grepColumnInclusionRule;
  private InclusionRule grepRoutineColumnInclusionRule;
  private InclusionRule grepDefinitionInclusionRule;
  private boolean grepInvertMatch;
  private boolean grepOnlyMatching;

  private boolean hideEmptyTables;

  private int childTableFilterDepth;
  private int parentTableFilterDepth;

  /**
   * Default options.
   */
  public SchemaCrawlerOptions()
  {
    informationSchemaViews = new InformationSchemaViews();
    databaseSpecificOverrideOptions = new DatabaseSpecificOverrideOptions();

    schemaInclusionRule = new IncludeAll();
    synonymInclusionRule = new ExcludeAll();
    sequenceInclusionRule = new ExcludeAll();

    tableTypes = new HashSet<>(Arrays.asList("TABLE", "VIEW"));
    tableInclusionRule = new IncludeAll();
    columnInclusionRule = new IncludeAll();

    routineTypes = new HashSet<>(Arrays.asList(RoutineType.procedure,
                                               RoutineType.function));
    routineInclusionRule = new IncludeAll();
    routineColumnInclusionRule = new IncludeAll();
  }

  /**
   * Options from properties.
   *
   * @param config
   *        Configuration properties
   */
  public SchemaCrawlerOptions(final Config config)
  {
    this();
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    informationSchemaViews = new InformationSchemaViews(config);
    databaseSpecificOverrideOptions = new DatabaseSpecificOverrideOptions(config);

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
      .getInclusionRule(SC_ROUTINE_PATTERN_INCLUDE, SC_ROUTINE_PATTERN_EXCLUDE);
    routineColumnInclusionRule = configProperties
      .getInclusionRule(SC_ROUTINE_COLUMN_PATTERN_INCLUDE,
                        SC_ROUTINE_COLUMN_PATTERN_EXCLUDE);

    grepColumnInclusionRule = configProperties
      .getInclusionRuleOrNull(SC_GREP_COLUMN_PATTERN_INCLUDE,
                              SC_GREP_COLUMN_PATTERN_EXCLUDE);
    grepRoutineColumnInclusionRule = configProperties
      .getInclusionRuleOrNull(SC_GREP_ROUTINE_COLUMN_PATTERN_INCLUDE,
                              SC_GREP_ROUTINE_COLUMN_PATTERN_EXCLUDE);
    grepDefinitionInclusionRule = configProperties
      .getInclusionRuleOrNull(SC_GREP_DEFINITION_PATTERN_INCLUDE,
                              SC_GREP_DEFINITION_PATTERN_EXCLUDE);

    grepInvertMatch = configProperties.getBooleanValue(SC_GREP_INVERT_MATCH);
    grepOnlyMatching = configProperties.getBooleanValue(SC_GREP_ONLY_MATCHING);

    hideEmptyTables = configProperties.getBooleanValue(SC_HIDE_EMPTY_TABLES);
  }

  public int getChildTableFilterDepth()
  {
    return childTableFilterDepth;
  }

  /**
   * Gets the column inclusion rule.
   *
   * @return Column inclusion rule.
   */
  public InclusionRule getColumnInclusionRule()
  {
    return columnInclusionRule;
  }

  /**
   * Gets database specific override options.
   */
  public DatabaseSpecificOverrideOptions getDatabaseSpecificOverrideOptions()
  {
    return databaseSpecificOverrideOptions;
  }

  /**
   * Gets the column inclusion rule for grep.
   *
   * @return Column inclusion rule for grep.
   */
  public InclusionRule getGrepColumnInclusionRule()
  {
    return grepColumnInclusionRule;
  }

  /**
   * Gets the definitions inclusion rule for grep.
   *
   * @return Definitions inclusion rule for grep.
   */
  public InclusionRule getGrepDefinitionInclusionRule()
  {
    return grepDefinitionInclusionRule;
  }

  /**
   * Gets the routine column rule for grep.
   *
   * @return Routine column rule for grep.
   */
  public InclusionRule getGrepRoutineColumnInclusionRule()
  {
    return grepRoutineColumnInclusionRule;
  }

  /**
   * Gets the information schema views.
   *
   * @return Information schema views.
   */
  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  public int getParentTableFilterDepth()
  {
    return parentTableFilterDepth;
  }

  /**
   * Gets the routine column rule.
   *
   * @return Routine column rule.
   */
  public InclusionRule getRoutineColumnInclusionRule()
  {
    return routineColumnInclusionRule;
  }

  /**
   * Gets the routine inclusion rule.
   *
   * @return Routine inclusion rule.
   */
  public InclusionRule getRoutineInclusionRule()
  {
    return routineInclusionRule;
  }

  public Collection<RoutineType> getRoutineTypes()
  {
    return new HashSet<>(routineTypes);
  }

  /**
   * Gets the schema inclusion rule.
   *
   * @return Schema inclusion rule.
   */
  public InclusionRule getSchemaInclusionRule()
  {
    return schemaInclusionRule;
  }

  /**
   * Gets the schema information level, identifying to what level the
   * schema should be crawled.
   *
   * @return Schema information level.
   */
  public SchemaInfoLevel getSchemaInfoLevel()
  {
    if (schemaInfoLevel == null)
    {
      return SchemaInfoLevel.standard();
    }
    else
    {
      return schemaInfoLevel;
    }
  }

  /**
   * Gets the sequence inclusion rule.
   *
   * @return Sequence inclusion rule.
   */
  public InclusionRule getSequenceInclusionRule()
  {
    return sequenceInclusionRule;
  }

  /**
   * Gets the synonym inclusion rule.
   *
   * @return Synonym inclusion rule.
   */
  public InclusionRule getSynonymInclusionRule()
  {
    return synonymInclusionRule;
  }

  /**
   * Gets the table inclusion rule.
   *
   * @return Table inclusion rule.
   */
  public InclusionRule getTableInclusionRule()
  {
    return tableInclusionRule;
  }

  /**
   * Gets the table name pattern.
   *
   * @return Table name pattern
   */
  public String getTableNamePattern()
  {
    return tableNamePattern;
  }

  /**
   * Returns the table types requested for output. This can be null, if
   * all supported table types are required in the output.
   *
   * @return All table types requested for output
   */
  public Collection<String> getTableTypes()
  {
    if (tableTypes == null)
    {
      return null;
    }
    else
    {
      return new HashSet<>(tableTypes);
    }
  }

  public boolean isGrepColumns()
  {
    return grepColumnInclusionRule != null;
  }

  public boolean isGrepDefinitions()
  {
    return grepDefinitionInclusionRule != null;
  }

  /**
   * Whether to invert matches.
   *
   * @return Whether to invert matches.
   */
  public boolean isGrepInvertMatch()
  {
    return grepInvertMatch;
  }

  /**
   * Whether grep includes show foreign keys that reference other
   * non-matching tables.
   */
  public boolean isGrepOnlyMatching()
  {
    return grepOnlyMatching;
  }

  public boolean isGrepRoutineColumns()
  {
    return grepRoutineColumnInclusionRule != null;
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that
   * is, tables with no rows of data) from the catalog.
   *
   * @return Whether to hide empty tables
   */
  public boolean isHideEmptyTables()
  {
    return hideEmptyTables;
  }

  public void setChildTableFilterDepth(final int childTableFilterDepth)
  {
    this.childTableFilterDepth = childTableFilterDepth;
  }

  /**
   * Sets the column inclusion rule.
   *
   * @param columnInclusionRule
   *        Column inclusion rule
   */
  public void setColumnInclusionRule(final InclusionRule columnInclusionRule)
  {
    this.columnInclusionRule = requireNonNull(columnInclusionRule,
                                              "Cannot use null value in a setter");
  }

  /**
   * Sets database specific override options.
   *
   * @param databaseSpecificOverrideOptions
   *        Database specific override options
   */
  public void setDatabaseSpecificOverrideOptions(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
  {
    if (databaseSpecificOverrideOptions == null)
    {
      this.databaseSpecificOverrideOptions = new DatabaseSpecificOverrideOptions();
    }
    else
    {
      this.databaseSpecificOverrideOptions = databaseSpecificOverrideOptions;
    }
  }

  /**
   * Sets the column inclusion rule for grep.
   *
   * @param grepColumnInclusionRule
   *        Column inclusion rule for grep
   */
  public void setGrepColumnInclusionRule(final InclusionRule grepColumnInclusionRule)
  {
    this.grepColumnInclusionRule = grepColumnInclusionRule;
  }

  /**
   * Sets the definition inclusion rule for grep.
   *
   * @param grepDefinitionInclusionRule
   *        Definition inclusion rule for grep
   */
  public void setGrepDefinitionInclusionRule(final InclusionRule grepDefinitionInclusionRule)
  {
    this.grepDefinitionInclusionRule = grepDefinitionInclusionRule;
  }

  /**
   * Set whether to invert matches.
   *
   * @param grepInvertMatch
   *        Whether to invert matches.
   */
  public void setGrepInvertMatch(final boolean grepInvertMatch)
  {
    this.grepInvertMatch = grepInvertMatch;
  }

  /**
   * Whether grep includes show foreign keys that reference other
   * non-matching tables.
   *
   * @param grepOnlyMatching
   *        Whether grep includes show foreign keys that reference other
   *        non-matching tables.
   */
  public void setGrepOnlyMatching(final boolean grepOnlyMatching)
  {
    this.grepOnlyMatching = grepOnlyMatching;
  }

  /**
   * Sets the routine column inclusion rule for grep.
   *
   * @param grepRoutineColumnInclusionRule
   *        Routine column inclusion rule for grep
   */
  public void setGrepRoutineColumnInclusionRule(final InclusionRule grepRoutineColumnInclusionRule)
  {
    this.grepRoutineColumnInclusionRule = grepRoutineColumnInclusionRule;
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that
   * is, tables with no rows of data) from the catalog.
   *
   * @param hideEmptyTables
   *        Whether to hide empty tables
   */
  public void setHideEmptyTables(final boolean hideEmptyTables)
  {
    this.hideEmptyTables = hideEmptyTables;
  }

  /**
   * Sets the information schema views.
   *
   * @param informationSchemaViews
   *        Information schema views.
   */
  public void setInformationSchemaViews(final InformationSchemaViews informationSchemaViews)
  {
    if (informationSchemaViews == null)
    {
      this.informationSchemaViews = new InformationSchemaViews();
    }
    else
    {
      this.informationSchemaViews = informationSchemaViews;
    }
  }

  public void setParentTableFilterDepth(final int parentTableFilterDepth)
  {
    this.parentTableFilterDepth = parentTableFilterDepth;
  }

  /**
   * Sets the routine column inclusion rule.
   *
   * @param routineColumnInclusionRule
   *        Routine column inclusion rule
   */
  public void setRoutineColumnInclusionRule(final InclusionRule routineColumnInclusionRule)
  {
    this.routineColumnInclusionRule = requireNonNull(routineColumnInclusionRule,
                                                     "Cannot use null value in a setter");
  }

  /**
   * Sets the routine inclusion rule.
   *
   * @param routineInclusionRule
   *        Routine inclusion rule
   */
  public void setRoutineInclusionRule(final InclusionRule routineInclusionRule)
  {
    this.routineInclusionRule = requireNonNull(routineInclusionRule,
                                               "Cannot use null value in a setter");
  }

  public void setRoutineTypes(final Collection<RoutineType> routineTypes)
  {
    if (routineTypes == null)
    {
      this.routineTypes = Collections.emptySet();
    }
    else
    {
      this.routineTypes = new HashSet<>(routineTypes);
    }
  }

  /**
   * Sets routine types from a comma-separated list of routine types.
   *
   * @param routineTypesString
   *        Comma-separated list of routine types.
   */
  public void setRoutineTypes(final String routineTypesString)
  {
    routineTypes = new HashSet<>();
    if (routineTypesString != null)
    {
      final String[] routineTypeStrings = routineTypesString.split(",");
      if (routineTypeStrings != null && routineTypeStrings.length > 0)
      {
        for (final String routineTypeString: routineTypeStrings)
        {
          routineTypes.add(RoutineType.valueOf(routineTypeString
            .toLowerCase(Locale.ENGLISH)));
        }
      }
    }
  }

  /**
   * Sets the schema inclusion rule.
   *
   * @param schemaInclusionRule
   *        Schema inclusion rule
   */
  public void setSchemaInclusionRule(final InclusionRule schemaInclusionRule)
  {
    this.schemaInclusionRule = requireNonNull(schemaInclusionRule,
                                              "Cannot use null value in a setter");
  }

  /**
   * Sets the schema information level, identifying to what level the
   * schema should be crawled.
   *
   * @param schemaInfoLevel
   *        Schema information level.
   */
  public void setSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    this.schemaInfoLevel = schemaInfoLevel;
  }

  /**
   * Sets the sequence inclusion rule.
   *
   * @param sequenceInclusionRule
   *        Sequence inclusion rule
   */
  public void setSequenceInclusionRule(final InclusionRule sequenceInclusionRule)
  {
    this.sequenceInclusionRule = requireNonNull(sequenceInclusionRule,
                                                "Cannot use null value in a setter");
  }

  /**
   * Sets the synonym inclusion rule.
   *
   * @param synonymInclusionRule
   *        Synonym inclusion rule
   */
  public void setSynonymInclusionRule(final InclusionRule synonymInclusionRule)
  {
    this.synonymInclusionRule = requireNonNull(synonymInclusionRule,
                                               "Cannot use null value in a setter");
  }

  /**
   * Sets the table inclusion rule.
   *
   * @param tableInclusionRule
   *        Table inclusion rule
   */
  public void setTableInclusionRule(final InclusionRule tableInclusionRule)
  {
    this.tableInclusionRule = requireNonNull(tableInclusionRule,
                                             "Cannot use null value in a setter");
  }

  /**
   * Sets the table name pattern, using the JDBC syntax for wildcards (_
   * and *). The table name pattern is case-sensitive, and matches just
   * the table name - not the fully qualified table name. The table name
   * pattern restricts the tables retrieved at an early stage in the
   * retrieval process, so it must be used only when performance needs
   * to be tuned.
   *
   * @param tableNamePattern
   *        Table name pattern
   */
  public void setTableNamePattern(final String tableNamePattern)
  {
    this.tableNamePattern = tableNamePattern;
  }

  /**
   * Sets table types requested for output from a collection of table
   * types. For example: TABLE,VIEW,SYSTEM_TABLE,GLOBAL
   * TEMPORARY,ALIAS,SYNONYM
   *
   * @param tableTypes
   *        Collection of table types. Can be null if all supported
   *        table types are requested.
   */
  public void setTableTypes(final Collection<String> tableTypes)
  {
    if (tableTypes == null)
    {
      this.tableTypes = null;
    }
    else
    {
      this.tableTypes = new HashSet<>(tableTypes);
    }
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
  public void setTableTypesFromString(final String tableTypesString)
  {
    if (tableTypesString != null)
    {
      tableTypes = new HashSet<>();
      final String[] tableTypeStrings = tableTypesString.split(",");
      if (tableTypeStrings != null && tableTypeStrings.length > 0)
      {
        for (final String tableTypeString: tableTypeStrings)
        {
          tableTypes.add(tableTypeString.trim());
        }
      }
    }
    else
    {
      tableTypes = null;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
