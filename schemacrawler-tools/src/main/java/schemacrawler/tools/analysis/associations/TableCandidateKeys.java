/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Index;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

final class TableCandidateKeys
  implements Iterable<Column>
{

  private final Table table;
  private final List<Column> tableKeys;

  TableCandidateKeys(final Table table)
  {
    this.table = requireNonNull(table);
    tableKeys = listTableKeys(table);
  }

  @Override
  public Iterator<Column> iterator()
  {
    return tableKeys.iterator();
  }

  @Override
  public String toString()
  {
    return String.format("%s: %s", table, tableKeys);
  }

  private List<Column> listTableKeys(final Table table)
  {
    final List<Column> tableKeys = new ArrayList<>();

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null && primaryKey.getColumns().size() == 1)
    {
      tableKeys.add(primaryKey.getColumns().get(0));
    }

    for (final Index index: table.getIndices())
    {
      if (index != null && index.isUnique() && index.getColumns().size() == 1)
      {
        tableKeys.add(index.getColumns().get(0));
      }
    }

    return tableKeys;
  }

}
