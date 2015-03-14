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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import schemacrawler.schema.Column;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

final class ColumnKeys
  implements Iterable<String>
{

  private final Table table;
  private final Map<String, Column> columnKeyMap;

  ColumnKeys(final Table table)
  {
    this.table = requireNonNull(table);
    columnKeyMap = mapColumnNameMatches(table);
  }

  public Column get(final String columnKey)
  {
    return columnKeyMap.get(columnKey);
  }

  @Override
  public Iterator<String> iterator()
  {
    return columnKeyMap.keySet().iterator();
  }

  private Map<String, Column> mapColumnNameMatches(final Table table)
  {
    final Map<String, Column> matchMap = new HashMap<>();

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null && primaryKey.getColumns().size() == 1)
    {
      matchMap.put("id", primaryKey.getColumns().get(0));
    }

    for (final Column column: table.getColumns())
    {
      String matchColumnName = column.getName().toLowerCase();
      if (matchColumnName.endsWith("_id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 3);
      }
      if (matchColumnName.endsWith("id") && !matchColumnName.equals("id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 2);
      }
      matchMap.put(matchColumnName, column);
    }

    return matchMap;
  }

  @Override
  public String toString()
  {
    return String.format("%s: %s", table, columnKeyMap);
  }

}
