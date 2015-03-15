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

package schemacrawler.utility;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
public final class MetaDataUtility
{

  public enum Connectivity
  {
    unknown,
    zero_one,
    zero_many,
    one_one;
  }

  public static Collection<List<String>> allIndexCoumnNames(final Table table)
  {
    return indexCoumnNames(table, false);
  }

  public static final List<String> columnNames(final Index index)
  {
    if (index == null)
    {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<>();
    for (final IndexColumn indexColumn: index.getColumns())
    {
      columnNames.add(indexColumn.getFullName());
    }
    return columnNames;
  }

  public static final List<String> foreignKeyColumnNames(final ColumnReference... columnReferences)
  {
    if (columnReferences == null)
    {
      return Collections.emptyList();
    }
    else
    {
      final List<String> columnNames = new ArrayList<>();
      for (final ColumnReference columnReference: columnReferences)
      {
        columnNames.add(columnReference.getForeignKeyColumn().getFullName());
      }
      return columnNames;
    }
  }

  public static final List<String> foreignKeyColumnNames(final ForeignKey foreignKey)
  {
    if (foreignKey == null)
    {
      return Collections.emptyList();
    }
    return foreignKeyColumnNames(foreignKey.getColumnReferences()
      .toArray(new ColumnReference[0]));
  }

  public static Connectivity getConnectivity(final Column fkColumn,
                                             final boolean isForeignKeyUnique)
  {
    if (fkColumn == null)
    {
      return Connectivity.unknown;
    }

    final boolean isColumnReference = fkColumn instanceof PartialDatabaseObject;

    final Connectivity connectivity;
    if (isColumnReference)
    {
      connectivity = Connectivity.unknown;
    }
    else if (isForeignKeyUnique)
    {
      connectivity = Connectivity.zero_one;
    }
    else
    {
      connectivity = Connectivity.zero_many;
    }
    return connectivity;
  }

  public static boolean isForeignKeyUnique(final ColumnReference... columnReferences)
  {
    if (columnReferences == null || columnReferences.length == 0)
    {
      return false;
    }
    final Table fkTable = columnReferences[0].getForeignKeyColumn().getParent();
    final Collection<List<String>> uniqueIndexCoumnNames = uniqueIndexCoumnNames(fkTable);
    final List<String> foreignKeyColumnNames = foreignKeyColumnNames(columnReferences);
    return uniqueIndexCoumnNames.contains(foreignKeyColumnNames);
  }

  public static boolean isForeignKeyUnique(final ForeignKey foreignKey)
  {
    if (foreignKey == null)
    {
      return false;
    }
    final ColumnReference[] columnReferences = foreignKey.getColumnReferences()
      .toArray(new ColumnReference[0]);
    return isForeignKeyUnique(columnReferences);
  }

  public static Collection<List<String>> uniqueIndexCoumnNames(final Table table)
  {
    return indexCoumnNames(table, true);
  }

  private static Collection<List<String>> indexCoumnNames(final Table table,
                                                          final boolean includeUniqueOnly)
  {
    final List<List<String>> allIndexCoumns = new ArrayList<>();

    final PrimaryKey primaryKey = table.getPrimaryKey();
    final List<String> pkColumns = columnNames(primaryKey);
    allIndexCoumns.add(pkColumns);

    for (final Index index: table.getIndices())
    {
      if (includeUniqueOnly && !index.isUnique())
      {
        continue;
      }

      final List<String> indexColumns = columnNames(index);
      allIndexCoumns.add(indexColumns);
    }
    return allIndexCoumns;
  }

  private MetaDataUtility()
  {
    // Prevent instantiation
  }

}
