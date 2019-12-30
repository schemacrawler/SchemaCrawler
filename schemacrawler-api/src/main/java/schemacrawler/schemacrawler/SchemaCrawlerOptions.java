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


import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

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

  private final int childTableFilterDepth;
  private final InclusionRule columnInclusionRule;
  private final InclusionRule grepColumnInclusionRule;
  private final InclusionRule grepDefinitionInclusionRule;
  private final boolean grepInvertMatch;
  private final boolean grepOnlyMatching;
  private final InclusionRule grepRoutineParameterInclusionRule;
  private final boolean isNoEmptyTables;
  private final int parentTableFilterDepth;
  private final InclusionRule routineInclusionRule;
  private final InclusionRule routineParameterInclusionRule;
  private final Collection<RoutineType> routineTypes;
  private final InclusionRule schemaInclusionRule;
  private final SchemaInfoLevel schemaInfoLevel;
  private final InclusionRule sequenceInclusionRule;
  private final InclusionRule synonymInclusionRule;
  private final InclusionRule tableInclusionRule;
  private final String tableNamePattern;
  private final Collection<String> tableTypes;

  SchemaCrawlerOptions(final SchemaInfoLevel schemaInfoLevel,
                       final InclusionRule schemaInclusionRule,
                       final InclusionRule synonymInclusionRule,
                       final InclusionRule sequenceInclusionRule,
                       final Collection<String> tableTypes,
                       final String tableNamePattern,
                       final InclusionRule tableInclusionRule,
                       final InclusionRule columnInclusionRule,
                       final Collection<RoutineType> routineTypes,
                       final InclusionRule routineInclusionRule,
                       final InclusionRule routineParameterInclusionRule,
                       final InclusionRule grepColumnInclusionRule,
                       final InclusionRule grepRoutineParameterInclusionRule,
                       final InclusionRule grepDefinitionInclusionRule,
                       final boolean grepInvertMatch,
                       final boolean grepOnlyMatching,
                       final boolean isNoEmptyTables,
                       final int childTableFilterDepth,
                       final int parentTableFilterDepth)
  {
    this.schemaInfoLevel = schemaInfoLevel;
    this.schemaInclusionRule = schemaInclusionRule;
    this.synonymInclusionRule = synonymInclusionRule;
    this.sequenceInclusionRule = sequenceInclusionRule;
    this.tableTypes = tableTypes;
    this.tableNamePattern = tableNamePattern;
    this.tableInclusionRule = tableInclusionRule;
    this.columnInclusionRule = columnInclusionRule;
    this.routineTypes = routineTypes;
    this.routineInclusionRule = routineInclusionRule;
    this.routineParameterInclusionRule = routineParameterInclusionRule;
    this.grepColumnInclusionRule = grepColumnInclusionRule;
    this.grepRoutineParameterInclusionRule = grepRoutineParameterInclusionRule;
    this.grepDefinitionInclusionRule = grepDefinitionInclusionRule;
    this.grepInvertMatch = grepInvertMatch;
    this.grepOnlyMatching = grepOnlyMatching;
    this.isNoEmptyTables = isNoEmptyTables;
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
  public Optional<InclusionRule> getGrepColumnInclusionRule()
  {
    return Optional.ofNullable(grepColumnInclusionRule);
  }

  /**
   * Gets the definitions inclusion rule for grep.
   *
   * @return Definitions inclusion rule for grep.
   */
  public Optional<InclusionRule> getGrepDefinitionInclusionRule()
  {
    return Optional.ofNullable(grepDefinitionInclusionRule);
  }

  /**
   * Gets the routine column rule for grep.
   *
   * @return Routine column rule for grep.
   */
  public Optional<InclusionRule> getGrepRoutineParameterInclusionRule()
  {
    return Optional.ofNullable(grepRoutineParameterInclusionRule);
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
  public InclusionRule getRoutineParameterInclusionRule()
  {
    return routineParameterInclusionRule;
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
   * Gets the schema information level, identifying to what level the schema
   * should be crawled.
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
   * Gets the table name pattern. A null value indicates do not take table
   * pattern into account.
   *
   * @return Table name pattern
   */
  public String getTableNamePattern()
  {
    return tableNamePattern;
  }

  /**
   * Returns the table types requested for output. This can be null, if all
   * supported table types are required in the output.
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
   * Whether grep includes show foreign keys that reference other non-matching
   * tables.
   */
  public boolean isGrepOnlyMatching()
  {
    return grepOnlyMatching;
  }

  public boolean isGrepRoutineParameters()
  {
    return grepRoutineParameterInclusionRule != null;
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that is, tables
   * with no rows of data) from the catalog.
   *
   * @return Whether to hide empty tables
   */
  public boolean isNoEmptyTables()
  {
    return isNoEmptyTables;
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
