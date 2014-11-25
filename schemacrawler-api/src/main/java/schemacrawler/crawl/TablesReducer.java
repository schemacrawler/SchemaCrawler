/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import static schemacrawler.filter.FilterFactory.tableFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import schemacrawler.filter.NamedObjectFilter;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class TablesReducer
  implements Reducer<Table>
{

  private final SchemaCrawlerOptions options;

  public TablesReducer(final SchemaCrawlerOptions options)
  {
    this.options = options;
  }

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
    // Filter for tables inclusion patterns (since we may be looping
    // over offline data), and grep patterns
    final NamedObjectFilter<Table> tableFilter = tableFilter(options);

    final Set<Table> reducedTables = new HashSet<>();
    for (final Table table: allTables)
    {
      if (tableFilter.include(table))
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

    final Set<Table> filteredTables = new HashSet<>();
    filteredTables.addAll(reducedTables);
    filteredTables.addAll(childTables);
    filteredTables.addAll(parentTables);
    return filteredTables;
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

  private void removeForeignKeys(final Collection<? extends Table> allTables)
  {

    for (final Table table: allTables)
    {
      for (final ForeignKey fk: table.getExportedForeignKeys())
      {
        for (final ForeignKeyColumnReference fkColumnReference: fk
          .getColumnReferences())
        {
          final Table referencedTable = fkColumnReference.getForeignKeyColumn()
            .getParent();
          boolean removeFk = false;
          if (isTablePartial(referencedTable))
          {
            removeFk = true;
          }
          else if (!allTables.contains(referencedTable))
          {
            removeFk = true;
          }

          if (removeFk)
          {
            if (options.isGrepOnlyMatching())
            {
              fk.setAttribute("foreignKey.filtered", true);
            }
            else
            {
              fk.setAttribute("foreignKey.filtered.foreignKeyColumn", true);
            }
          }
        }
      }

      for (final ForeignKey fk: table.getImportedForeignKeys())
      {
        for (final ForeignKeyColumnReference fkColumnReference: fk
          .getColumnReferences())
        {
          final TableReference referencedTable = fkColumnReference
            .getPrimaryKeyColumn().getParent();
          boolean removeFk = false;
          if (!(referencedTable instanceof MutableTable))
          {
            removeFk = true;
          }
          else if (!allTables.contains(referencedTable))
          {
            removeFk = true;
          }

          if (removeFk)
          {
            if (options.isGrepOnlyMatching())
            {
              fk.setAttribute("foreignKey.filtered", true);
            }
            else
            {
              fk.setAttribute("foreignKey.filtered.primaryKeyColumn", true);
            }
          }
        }
      }
    }
  }

}
