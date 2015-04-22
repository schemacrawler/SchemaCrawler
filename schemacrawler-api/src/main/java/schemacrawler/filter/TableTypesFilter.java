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
package schemacrawler.filter;


import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class TableTypesFilter
  implements Predicate<Table>
{

  private final Collection<String> tableTypes;

  public TableTypesFilter(final SchemaCrawlerOptions options)
  {
    if (options != null)
    {
      final Collection<String> tableTypesOptions = options.getTableTypes();
      if (tableTypesOptions == null)
      {
        tableTypes = null;
      }
      else
      {
        tableTypes = new HashSet<>();
        for (final String tableType: tableTypesOptions)
        {
          tableTypes.add(tableType.toLowerCase());
        }
      }
    }
    else
    {
      tableTypes = null;
    }
  }

  /**
   * Check for table limiting rules.
   *
   * @param table
   *        Table to check
   * @return Whether the table should be included
   */
  @Override
  public boolean test(final Table table)
  {
    final boolean include;

    if (tableTypes != null)
    {
      include = tableTypes.contains(table.getTableType().getTableType()
        .toLowerCase());
    }
    else
    {
      include = true;
    }

    return include;
  }

}
