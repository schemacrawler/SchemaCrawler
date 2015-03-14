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

import java.util.List;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import sf.util.Multimap;

final class ColumnMatchKeysMap
{

  private final List<Table> tables;
  private final Multimap<String, Column> columnMatchKeysMap;

  ColumnMatchKeysMap(final List<Table> tables)
  {
    this.tables = requireNonNull(tables);
    columnMatchKeysMap = new Multimap<>();

    for (final Table table: tables)
    {
      mapColumnNameMatches(table);
    }
  }

  public boolean containsKey(final String columnKey)
  {
    return columnMatchKeysMap.containsKey(columnKey);
  }

  public List<Column> get(final String columnKey)
  {
    return columnMatchKeysMap.get(columnKey);
  }

  private void mapColumnNameMatches(final Table table)
  {
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
      if (!matchColumnName.equals("id"))
      {
        columnMatchKeysMap.add(matchColumnName, column);
      }
    }
  }

  @Override
  public String toString()
  {
    return columnMatchKeysMap.toString();
  }

}
