/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.tools.analysis.associations;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Table;

final class ForeignKeys
{

  private final Collection<ColumnReference> foreignKeys;

  ForeignKeys(final List<Table> tables)
  {
    foreignKeys = mapForeignKeyColumns(tables);
  }

  public boolean contains(final ColumnReference columnMap)
  {
    if (columnMap == null)
    {
      return false;
    }

    // We have to loop through the collection, since we want to use the
    // equals from the WeakAssociation
    for (final ColumnReference foreignKey: foreignKeys)
    {
      final boolean equals = columnMap.equals(foreignKey);
      if (equals)
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString()
  {
    return foreignKeys.toString();
  }

  private Collection<ColumnReference> mapForeignKeyColumns(final List<Table> tables)
  {
    requireNonNull(tables);

    final Collection<ColumnReference> fkColumnsMap = new HashSet<>();
    for (final Table table: tables)
    {
      for (final ForeignKey foreignKey: table.getForeignKeys())
      {
        for (final ForeignKeyColumnReference columnRef: foreignKey)
        {
          fkColumnsMap.add(columnRef);
        }
      }
    }
    return fkColumnsMap;
  }
}
