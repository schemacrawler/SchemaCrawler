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
package schemacrawler.schemacrawler;


import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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

  private static Collection<RoutineType> allRoutineTypes()
  {
    return Arrays.asList(RoutineType.procedure, RoutineType.function);
  }

  private static Collection<String> defaultTableTypes()
  {
    return Arrays.asList("BASE TABLE", "TABLE", "VIEW");
  }

  private SchemaInfoLevel schemaInfoLevel;

  private final String title;

  private InclusionRule schemaInclusionRule;
  private InclusionRule synonymInclusionRule;
  private InclusionRule sequenceInclusionRule;

  private final Collection<String> tableTypes;
  private String tableNamePattern;
  private InclusionRule tableInclusionRule;
  private final InclusionRule columnInclusionRule;

  private final Collection<RoutineType> routineTypes;
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
    schemaInfoLevel = SchemaInfoLevelBuilder.standard();

    title = "";

    // All schemas are included by default
    schemaInclusionRule = new IncludeAll();

    synonymInclusionRule = new ExcludeAll();
    sequenceInclusionRule = new ExcludeAll();

    // Note: Of the database objects, only tables are included by
    // default
    tableTypes = defaultTableTypes();
    tableInclusionRule = new IncludeAll();
    columnInclusionRule = new IncludeAll();

    routineTypes = allRoutineTypes();
    routineInclusionRule = new ExcludeAll();
    routineColumnInclusionRule = new ExcludeAll();

  }

  SchemaCrawlerOptions(final SchemaInfoLevel schemaInfoLevel,
                       final String title,
                       final InclusionRule schemaInclusionRule,
                       final InclusionRule synonymInclusionRule,
                       final InclusionRule sequenceInclusionRule,
                       final Collection<String> tableTypes,
                       final String tableNamePattern,
                       final InclusionRule tableInclusionRule,
                       final InclusionRule columnInclusionRule,
                       final Collection<RoutineType> routineTypes,
                       final InclusionRule routineInclusionRule,
                       final InclusionRule routineColumnInclusionRule,
                       final InclusionRule grepColumnInclusionRule,
                       final InclusionRule grepRoutineColumnInclusionRule,
                       final InclusionRule grepDefinitionInclusionRule,
                       final boolean grepInvertMatch,
                       final boolean grepOnlyMatching,
                       final boolean hideEmptyTables,
                       final int childTableFilterDepth,
                       final int parentTableFilterDepth)
  {
    this.schemaInfoLevel = schemaInfoLevel;
    this.title = title;
    this.schemaInclusionRule = schemaInclusionRule;
    this.synonymInclusionRule = synonymInclusionRule;
    this.sequenceInclusionRule = sequenceInclusionRule;
    this.tableTypes = tableTypes;
    this.tableNamePattern = tableNamePattern;
    this.tableInclusionRule = tableInclusionRule;
    this.columnInclusionRule = columnInclusionRule;
    this.routineTypes = routineTypes;
    this.routineInclusionRule = routineInclusionRule;
    this.routineColumnInclusionRule = routineColumnInclusionRule;
    this.grepColumnInclusionRule = grepColumnInclusionRule;
    this.grepRoutineColumnInclusionRule = grepRoutineColumnInclusionRule;
    this.grepDefinitionInclusionRule = grepDefinitionInclusionRule;
    this.grepInvertMatch = grepInvertMatch;
    this.grepOnlyMatching = grepOnlyMatching;
    this.hideEmptyTables = hideEmptyTables;
    this.childTableFilterDepth = childTableFilterDepth;
    this.parentTableFilterDepth = parentTableFilterDepth;
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
    if (routineTypes == null)
    {
      return null;
    }
    else
    {
      return new HashSet<>(routineTypes);
    }
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
    return schemaInfoLevel;
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
   * Gets the table name pattern. A null value indicates do not take
   * table pattern into account.
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

  public String getTitle()
  {
    return title;
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
    this.schemaInfoLevel = requireNonNull(schemaInfoLevel,
                                          "No schema information level provided");
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
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return ObjectToString.toString(this);
  }

}
