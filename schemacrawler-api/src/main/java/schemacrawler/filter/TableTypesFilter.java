/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.filter;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class TableTypesFilter
  implements Predicate<Table>
{

  private final Collection<String> tableTypes;

  public TableTypesFilter()
  {
    tableTypes = null;
  }

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

  public TableTypesFilter(final String... tableTypesFiltered)
  {
    if (tableTypesFiltered != null)
    {
      final Collection<String> tableTypesOptions = Arrays
        .asList(tableTypesFiltered);
      tableTypes = new HashSet<>();
      for (final String tableType: tableTypesOptions)
      {
        tableTypes.add(tableType.toLowerCase());
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
      include = tableTypes
        .contains(table.getTableType().getTableType().toLowerCase());
    }
    else
    {
      include = true;
    }

    return include;
  }

}
