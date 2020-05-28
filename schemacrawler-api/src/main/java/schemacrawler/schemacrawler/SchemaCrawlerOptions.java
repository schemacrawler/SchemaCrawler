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


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import schemacrawler.inclusionrule.InclusionRule;
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

  private final InclusionRule columnInclusionRule;
  private final InclusionRule routineInclusionRule;
  private final InclusionRule routineParameterInclusionRule;
  private final Collection<RoutineType> routineTypes;
  private final InclusionRule schemaInclusionRule;
  private final InclusionRule sequenceInclusionRule;
  private final InclusionRule synonymInclusionRule;
  private final InclusionRule tableInclusionRule;
  private final String tableNamePattern;
  private final Collection<String> tableTypes;
  private final FilterOptions filterOptions;
  private final GrepOptions grepOptions;
  private final LoadOptions loadOptions;

  SchemaCrawlerOptions(final InclusionRule schemaInclusionRule,
                       final InclusionRule synonymInclusionRule,
                       final InclusionRule sequenceInclusionRule,
                       final Collection<String> tableTypes,
                       final String tableNamePattern,
                       final InclusionRule tableInclusionRule,
                       final InclusionRule columnInclusionRule,
                       final Collection<RoutineType> routineTypes,
                       final InclusionRule routineInclusionRule,
                       final InclusionRule routineParameterInclusionRule,
                       final FilterOptions filterOptions,
                       final GrepOptions grepOptions,
                       final LoadOptions loadOptions)
  {
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
    this.filterOptions = requireNonNull(filterOptions, "No filter options provided");
    this.grepOptions = requireNonNull(grepOptions, "No grep options provided");
    this.loadOptions = requireNonNull(loadOptions, "No load options provided");
  }

  @Deprecated
  public int getChildTableFilterDepth()
  {
    return filterOptions.getChildTableFilterDepth();
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

  public GrepOptions getGrepOptions()
  {
    return grepOptions;
  }

  /**
   * Gets the column inclusion rule for grep.
   *
   * @return Column inclusion rule for grep.
   * @deprecated
   */
  @Deprecated
  public Optional<InclusionRule> getGrepColumnInclusionRule()
  {
    return grepOptions.getGrepColumnInclusionRule();
  }

  /**
   * Gets the definitions inclusion rule for grep.
   *
   * @return Definitions inclusion rule for grep.
   * @deprecated
   */
  @Deprecated
  public Optional<InclusionRule> getGrepDefinitionInclusionRule()
  {
    return grepOptions.getGrepDefinitionInclusionRule();
  }

  /**
   * Gets the routine column rule for grep.
   *
   * @return Routine column rule for grep.
   * @deprecated
   */
  @Deprecated
  public Optional<InclusionRule> getGrepRoutineParameterInclusionRule()
  {
    return grepOptions.getGrepRoutineParameterInclusionRule();
  }

  @Deprecated
  public int getParentTableFilterDepth()
  {
    return filterOptions.getParentTableFilterDepth();
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
   * @deprecated
   */
  @Deprecated
  public SchemaInfoLevel getSchemaInfoLevel()
  {
    return loadOptions.getSchemaInfoLevel();
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

  @Deprecated
  public boolean isGrepColumns()
  {
    return grepOptions.isGrepColumns();
  }

  @Deprecated
  public boolean isGrepDefinitions()
  {
    return grepOptions.isGrepDefinitions();
  }

  /**
   * Whether to invert matches.
   *
   * @return Whether to invert matches.
   * @deprecated
   */
  @Deprecated
  public boolean isGrepInvertMatch()
  {
    return grepOptions.isGrepInvertMatch();
  }

  /**
   * Whether grep includes show foreign keys that reference other non-matching
   * tables.
   * @deprecated
   */
  @Deprecated
  public boolean isGrepOnlyMatching()
  {
    return grepOptions.isGrepOnlyMatching();
  }

  @Deprecated
  public boolean isGrepRoutineParameters()
  {
    return grepOptions.isGrepRoutineParameters();
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that is, tables
   * with no rows of data) from the catalog.
   *
   * @return Whether to hide empty tables
   * @deprecated
   */
  @Deprecated
  public boolean isNoEmptyTables()
  {
    return filterOptions.isNoEmptyTables();
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that is, tables
   * with no rows of data) from the catalog.
   *
   * @return Whether to hide empty tables
   * @deprecated
   */
  @Deprecated
  public boolean isLoadRowCounts()
  {
    return loadOptions.isLoadRowCounts();
  }

  public LoadOptions getLoadOptions()
  {
    return loadOptions;
  }

  public FilterOptions getFilterOptions()
  {
    return filterOptions;
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
