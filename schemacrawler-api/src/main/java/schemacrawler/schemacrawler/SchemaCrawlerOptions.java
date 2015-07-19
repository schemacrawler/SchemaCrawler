/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
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
import static sf.util.Utility.isBlank;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

  private static final long serialVersionUID = -3557794862382066029L;

  private SchemaInfoLevel schemaInfoLevel;

  private String title;

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
   * Gets the schema information level, identifying to what level the schema
   * should be crawled.
   *
   * @return Schema information level.
   */
  public SchemaInfoLevel getSchemaInfoLevel()
  {
    if (schemaInfoLevel == null)
    {
      return SchemaInfoLevelBuilder.standard();
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

  public String getTitle()
  {
    return isBlank(title)? "": title;
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

  public boolean isGrepRoutineColumns()
  {
    return grepRoutineColumnInclusionRule != null;
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that is, tables
   * with no rows of data) from the catalog.
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
   * Sets the column inclusion rule for grep.
   *
   * @param grepColumnInclusionRule
   *        Column inclusion rule for grep
   */
  public void
    setGrepColumnInclusionRule(final InclusionRule grepColumnInclusionRule)
  {
    this.grepColumnInclusionRule = grepColumnInclusionRule;
  }

  /**
   * Sets the definition inclusion rule for grep.
   *
   * @param grepDefinitionInclusionRule
   *        Definition inclusion rule for grep
   */
  public void
    setGrepDefinitionInclusionRule(final InclusionRule grepDefinitionInclusionRule)
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
   * Whether grep includes show foreign keys that reference other non-matching
   * tables.
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
  public void
    setGrepRoutineColumnInclusionRule(final InclusionRule grepRoutineColumnInclusionRule)
  {
    this.grepRoutineColumnInclusionRule = grepRoutineColumnInclusionRule;
  }

  /**
   * If infolevel=maximum, this option will remove empty tables (that is, tables
   * with no rows of data) from the catalog.
   *
   * @param hideEmptyTables
   *        Whether to hide empty tables
   */
  public void setHideEmptyTables(final boolean hideEmptyTables)
  {
    this.hideEmptyTables = hideEmptyTables;
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
  public void
    setRoutineColumnInclusionRule(final InclusionRule routineColumnInclusionRule)
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
   * Sets the schema information level, identifying to what level the schema
   * should be crawled.
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
  public void
    setSequenceInclusionRule(final InclusionRule sequenceInclusionRule)
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
   * Sets the table name pattern, using the JDBC syntax for wildcards (_ and *).
   * The table name pattern is case-sensitive, and matches just the table name -
   * not the fully qualified table name. The table name pattern restricts the
   * tables retrieved at an early stage in the retrieval process, so it must be
   * used only when performance needs to be tuned.
   *
   * @param tableNamePattern
   *        Table name pattern
   */
  public void setTableNamePattern(final String tableNamePattern)
  {
    this.tableNamePattern = tableNamePattern;
  }

  /**
   * Sets table types requested for output from a collection of table types. For
   * example: TABLE,VIEW,SYSTEM_TABLE,GLOBAL TEMPORARY,ALIAS,SYNONYM
   *
   * @param tableTypes
   *        Collection of table types. Can be null if all supported table types
   *        are requested.
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

  public void setTitle(final String title)
  {
    this.title = title;
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
