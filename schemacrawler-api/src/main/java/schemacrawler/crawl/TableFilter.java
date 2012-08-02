/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.crawl;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class TableFilter
{

  private static final Logger LOGGER = Logger.getLogger(TableFilter.class
    .getName());

  private final SchemaCrawlerOptions options;
  private final NamedObjectList<MutableTable> allTables;

  public TableFilter(final SchemaCrawlerOptions options,
                     final NamedObjectList<MutableTable> allTables)
  {
    this.options = options;
    this.allTables = allTables;
  }

  public void filter()
  {
    final Collection<MutableTable> filteredTables = doFilter();
    for (final MutableTable table: allTables)
    {
      if (!filteredTables.contains(table))
      {
        allTables.remove(table);
      }
    }
  }

  private Collection<MutableTable> doFilter()
  {
    // Filter for grep
    final Set<MutableTable> greppedTables = new HashSet<MutableTable>();
    for (final MutableTable table: allTables)
    {
      if (grepMatch(options, table))
      {
        greppedTables.add(table);
      }
    }

    // Add in referenced tables
    final int childTableFilterDepth = options.getChildTableFilterDepth();
    final Collection<MutableTable> childTables = includeRelatedTables(TableRelationshipType.child,
                                                                      childTableFilterDepth,
                                                                      greppedTables);
    final int parentTableFilterDepth = options.getParentTableFilterDepth();
    final Collection<MutableTable> parentTables = includeRelatedTables(TableRelationshipType.parent,
                                                                       parentTableFilterDepth,
                                                                       greppedTables);

    final Set<MutableTable> filteredTables = new HashSet<MutableTable>();
    filteredTables.addAll(greppedTables);
    filteredTables.addAll(childTables);
    filteredTables.addAll(parentTables);
    return filteredTables;
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table
   * column inclusion rule is found, and at least one column matches the
   * rule.
   * 
   * @param options
   *        Options
   * @param table
   *        Table to check
   * @return Whether the column should be included
   */
  private boolean grepMatch(final SchemaCrawlerOptions options,
                            final Table table)
  {
    final boolean invertMatch = options.isGrepInvertMatch();
    final boolean checkIncludeForColumns = options.isGrepColumns();
    final boolean checkIncludeForDefinitions = options.isGrepDefinitions();

    final InclusionRule grepColumnInclusionRule = options
      .getGrepColumnInclusionRule();
    final InclusionRule grepDefinitionInclusionRule = options
      .getGrepDefinitionInclusionRule();

    if (!checkIncludeForColumns && !checkIncludeForDefinitions)
    {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    for (final Column column: table.getColumns())
    {
      if (checkIncludeForColumns)
      {
        if (grepColumnInclusionRule.include(column.getFullName()))
        {
          includeForColumns = true;
          break;
        }
      }
      if (checkIncludeForDefinitions)
      {
        if (grepDefinitionInclusionRule.include(column.getRemarks()))
        {
          includeForDefinitions = true;
          break;
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.include(table.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (table instanceof View)
      {
        if (grepDefinitionInclusionRule.include(((View) table).getDefinition()))
        {
          includeForDefinitions = true;
        }
      }
      for (final Trigger trigger: table.getTriggers())
      {
        if (grepDefinitionInclusionRule.include(trigger.getActionStatement()))
        {
          includeForDefinitions = true;
          break;
        }
      }
    }

    boolean include = checkIncludeForColumns && includeForColumns
                      || checkIncludeForDefinitions && includeForDefinitions;
    if (invertMatch)
    {
      include = !include;
    }

    if (!include)
    {
      LOGGER.log(Level.FINE, "Removing table " + table
                             + " since it does not match the grep pattern");
    }

    return include;
  }

  private Collection<MutableTable> includeRelatedTables(final TableRelationshipType tableRelationshipType,
                                                        final int depth,
                                                        final Set<MutableTable> greppedTables)
  {
    final Set<MutableTable> includedTables = new HashSet<MutableTable>();
    includedTables.addAll(greppedTables);

    for (int i = 0; i < depth; i++)
    {
      for (final MutableTable table: new HashSet<MutableTable>(includedTables))
      {
        for (final Table relatedTable: table
          .getRelatedTables(tableRelationshipType))
        {
          includedTables.add((MutableTable) relatedTable);
        }
      }
    }

    return includedTables;
  }

}
