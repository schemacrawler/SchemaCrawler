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


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.BaseForeignKey;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.Index;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;

/**
 * SchemaCrawler utility methods.
 *
 * @author sfatehi
 */
public final class MetaDataUtility
{

  public enum ForeignKeyCardinality
  {
    unknown(""),
    zero_one("(0..1)"),
    zero_many("(0..many)"),
    one_one("(1..1)");

    private final String description;

    private ForeignKeyCardinality(final String description)
    {
      this.description = requireNonNull(description);
    }

    @Override
    public String toString()
    {
      return description;
    }

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
    for (final Column indexColumn: index)
    {
      columnNames.add(indexColumn.getFullName());
    }
    return columnNames;
  }

  public static String constructForeignKeyName(final Column pkColumn,
                                               final Column fkColumn)
  {
    final TableReference pkTable = requireNonNull(pkColumn).getParent();
    final TableReference fkParent = requireNonNull(fkColumn).getParent();
    final String pkHex = Integer.toHexString(pkTable.getFullName().hashCode());
    final String fkHex = Integer.toHexString(fkParent.getFullName().hashCode());
    final String foreignKeyName = String.format("SC_%s_%s", pkHex, fkHex)
      .toUpperCase();
    return foreignKeyName;
  }

  public static ForeignKeyCardinality findForeignKeyCardinality(final BaseForeignKey foreignKey)
  {
    if (foreignKey == null)
    {
      return ForeignKeyCardinality.unknown;
    }
    final boolean isForeignKeyUnique = isForeignKeyUnique(foreignKey);

    final ColumnReference columnRef0 = (ColumnReference) foreignKey
      .getColumnReferences().get(0);
    final Column fkColumn = columnRef0.getForeignKeyColumn();
    final boolean isColumnReference = fkColumn instanceof PartialDatabaseObject;

    final ForeignKeyCardinality connectivity;
    if (isColumnReference)
    {
      connectivity = ForeignKeyCardinality.unknown;
    }
    else if (isForeignKeyUnique)
    {
      connectivity = ForeignKeyCardinality.zero_one;
    }
    else
    {
      connectivity = ForeignKeyCardinality.zero_many;
    }
    return connectivity;
  }

  public static final List<String> foreignKeyColumnNames(final BaseForeignKey<? extends ColumnReference> foreignKey)
  {
    if (foreignKey == null)
    {
      return Collections.emptyList();
    }
    final List<String> columnNames = new ArrayList<>();
    for (final ColumnReference columnReference: foreignKey)
    {
      columnNames.add(columnReference.getForeignKeyColumn().getFullName());
    }
    return columnNames;
  }

  public static boolean isForeignKeyUnique(final BaseForeignKey foreignKey)
  {
    if (foreignKey == null)
    {
      return false;
    }
    final ColumnReference columnRef0 = (ColumnReference) foreignKey
      .getColumnReferences().get(0);
    final Table fkTable = columnRef0.getForeignKeyColumn().getParent();
    final Collection<List<String>> uniqueIndexCoumnNames = uniqueIndexCoumnNames(fkTable);
    final List<String> foreignKeyColumnNames = foreignKeyColumnNames(foreignKey);
    return uniqueIndexCoumnNames.contains(foreignKeyColumnNames);
  }

  public static Collection<List<String>> uniqueIndexCoumnNames(final Table table)
  {
    return indexCoumnNames(table, true);
  }

  private static Collection<List<String>> indexCoumnNames(final Table table,
                                                          final boolean includeUniqueOnly)
  {
    final List<List<String>> allIndexCoumns = new ArrayList<>();
    if (table instanceof PartialDatabaseObject)
    {
      return allIndexCoumns;
    }

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
