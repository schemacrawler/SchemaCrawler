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
import java.util.List;

import schemacrawler.schema.Column;

final class ColumnMatchKeys
{

  private final Column column;
  private final List<String> columnMatchKeys;

  ColumnMatchKeys(final Column column)
  {
    this.column = requireNonNull(column);
    columnMatchKeys = columnMatchKeys(column);
  }

  public List<String> get()
  {
    return columnMatchKeys;
  }

  private List<String> columnMatchKeys(final Column column)
  {
    final List<String> matchMap = new ArrayList<>();

    String matchColumnName = column.getName().toLowerCase();
    if (matchColumnName.endsWith("_id"))
    {
      matchColumnName = matchColumnName.substring(0,
                                                  matchColumnName.length() - 3);
    }
    if (matchColumnName.endsWith("id") && !matchColumnName.equals("id"))
    {
      matchColumnName = matchColumnName.substring(0,
                                                  matchColumnName.length() - 2);
    }
    matchMap.add(matchColumnName);

    return matchMap;
  }

  @Override
  public String toString()
  {
    return String.format("%s: %s", column, columnMatchKeys);
  }

}
