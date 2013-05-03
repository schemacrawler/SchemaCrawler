/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
import java.util.logging.Logger;

import schemacrawler.crawl.filter.DatabaseObjectFilter;
import schemacrawler.crawl.filter.FilterFactory;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
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

    removeForeignKeys();
  }

  private Collection<MutableTable> doFilter()
  {
    // Filter for grep
    final DatabaseObjectFilter<Table> grepFilter = FilterFactory
      .grepFilter(options);
    final Set<MutableTable> greppedTables = new HashSet<>();
    for (final MutableTable table: allTables)
    {
      if (grepFilter.include(table))
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

    final Set<MutableTable> filteredTables = new HashSet<>();
    filteredTables.addAll(greppedTables);
    filteredTables.addAll(childTables);
    filteredTables.addAll(parentTables);
    return filteredTables;
  }

  private Collection<MutableTable> includeRelatedTables(final TableRelationshipType tableRelationshipType,
                                                        final int depth,
                                                        final Set<MutableTable> greppedTables)
  {
    final Set<MutableTable> includedTables = new HashSet<>();
    includedTables.addAll(greppedTables);

    for (int i = 0; i < depth; i++)
    {
      for (final MutableTable table: new HashSet<>(includedTables))
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

  private void removeForeignKeys()
  {

    for (final MutableTable table: allTables)
    {
      for (final ForeignKey fk: table.getExportedForeignKeys())
      {
        for (final ForeignKeyColumnReference fkColumnReference: fk
          .getColumnReferences())
        {
          final Table referencedTable = fkColumnReference.getForeignKeyColumn()
            .getParent();
          boolean removeFk = false;
          if (!(referencedTable instanceof MutableTable))
          {
            removeFk = true;
          }
          else if (!allTables.contains((MutableTable) referencedTable))
          {
            removeFk = true;
          }

          if (removeFk)
          {
            if (options.isGrepOnlyMatching())
            {
              table.removeForeignKey(fk.getFullName());
            }
            else
            {
              // Replace reference with a column partial
              final ColumnPartial columnPartial = new ColumnPartial(fkColumnReference
                .getForeignKeyColumn());
              ((TablePartial) columnPartial.getParent()).addForeignKey(fk);
              ((MutableForeignKeyColumnReference) fkColumnReference)
                .setForeignKeyColumn(columnPartial);
            }
          }
        }
      }

      for (final ForeignKey fk: table.getImportedForeignKeys())
      {
        for (final ForeignKeyColumnReference fkColumnReference: fk
          .getColumnReferences())
        {
          final Table referencedTable = fkColumnReference.getPrimaryKeyColumn()
            .getParent();
          boolean removeFk = false;
          if (!(referencedTable instanceof MutableTable))
          {
            removeFk = true;
          }
          else if (!allTables.contains((MutableTable) referencedTable))
          {
            removeFk = true;
          }

          if (removeFk)
          {
            if (options.isGrepOnlyMatching())
            {
              table.removeForeignKey(fk.getFullName());
            }
            else
            {
              // Replace reference with a column partial
              final ColumnPartial columnPartial = new ColumnPartial(fkColumnReference
                .getPrimaryKeyColumn());
              ((TablePartial) columnPartial.getParent()).addForeignKey(fk);
              ((MutableForeignKeyColumnReference) fkColumnReference)
                .setPrimaryKeyColumn(columnPartial);
            }
          }
        }
      }
    }
  }

}
