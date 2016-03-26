/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.analysis.associations;


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
