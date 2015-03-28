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
package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class TablesReducer
  implements Reducer<Table>
{

  private final SchemaCrawlerOptions options;
  private final NamedObjectFilter<Table> tableFilter;

  public TablesReducer(final SchemaCrawlerOptions options,
                       final NamedObjectFilter<Table> tableFilter)
  {
    this.options = requireNonNull(options);
    this.tableFilter = requireNonNull(tableFilter);
  }

  @Override
  public void reduce(final Collection<? extends Table> allTables)
  {
    if (allTables == null)
    {
      return;
    }
    allTables.retainAll(doReduce(allTables));

    removeForeignKeys(allTables);
  }

  private Collection<Table> doReduce(final Collection<? extends Table> allTables)
  {
    // Filter tables, keeping the ones we need
    final Set<Table> reducedTables = new HashSet<>();
    for (final Table table: allTables)
    {
      if (tableFilter.test(table))
      {
        reducedTables.add(table);
      }
    }

    // Add in referenced tables
    final int childTableFilterDepth = options.getChildTableFilterDepth();
    final Collection<Table> childTables = includeRelatedTables(TableRelationshipType.child,
                                                               childTableFilterDepth,
                                                               reducedTables);
    final int parentTableFilterDepth = options.getParentTableFilterDepth();
    final Collection<Table> parentTables = includeRelatedTables(TableRelationshipType.parent,
                                                                parentTableFilterDepth,
                                                                reducedTables);

    final Set<Table> keepTables = new HashSet<>();
    keepTables.addAll(reducedTables);
    keepTables.addAll(childTables);
    keepTables.addAll(parentTables);

    // Mark tables as being filtered out
    for (final Table table: allTables)
    {
      if (isTablePartial(table) || !keepTables.contains(table))
      {
        markTableFilteredOut(table);
      }
    }

    return keepTables;
  }

  private Collection<Table> includeRelatedTables(final TableRelationshipType tableRelationshipType,
                                                 final int depth,
                                                 final Set<Table> greppedTables)
  {
    final Set<Table> includedTables = new HashSet<>();
    includedTables.addAll(greppedTables);

    for (int i = 0; i < depth; i++)
    {
      for (final Table table: new HashSet<>(includedTables))
      {
        for (final TableReference relatedTable: table
          .getRelatedTables(tableRelationshipType))
        {
          if (!isTablePartial(relatedTable))
          {
            includedTables.add((Table) relatedTable);
          }
        }
      }
    }

    return includedTables;
  }

  private boolean isTablePartial(final TableReference table)
  {
    return table instanceof PartialDatabaseObject;
  }

  private void markTableFilteredOut(final TableReference referencedTable)
  {
    referencedTable.setAttribute("schemacrawler.table.filtered_out", true);
    if (options.isGrepOnlyMatching())
    {
      referencedTable.setAttribute("schemacrawler.table.no_grep_match", true);
    }
  }

  private void removeForeignKeys(final Collection<? extends Table> allTables)
  {
    for (final Table table: allTables)
    {
      for (final ForeignKey foreignKey: table.getExportedForeignKeys())
      {
        for (final ForeignKeyColumnReference fkColumnRef: foreignKey)
        {
          final TableReference referencedTable = fkColumnRef
            .getForeignKeyColumn().getParent();
          if (isTablePartial(referencedTable)
              || !allTables.contains(referencedTable))
          {
            markTableFilteredOut(referencedTable);
          }
        }
      }
    }
  }

}
