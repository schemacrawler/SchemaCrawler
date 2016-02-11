/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.linter;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.crawl.NotLoadedException;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class LinterNullColumnsInIndex
  extends BaseLinter
{

  @Override
  public String getSummary()
  {
    return "unique index with nullable columns";
  }

  @Override
  protected void lint(final Table table, final Connection connection)
  {
    requireNonNull(table, "No table provided");

    final List<Index> nullableColumnsInUniqueIndex = findNullableColumnsInUniqueIndex(table
      .getIndexes());
    for (final Index index: nullableColumnsInUniqueIndex)
    {
      addTableLint(table, getSummary(), index);
    }
  }

  private List<Index> findNullableColumnsInUniqueIndex(final Collection<Index> indexes)
  {
    final List<Index> nullableColumnsInUniqueIndex = new ArrayList<>();
    for (final Index index: indexes)
    {
      if (index.isUnique())
      {
        for (final IndexColumn indexColumn: index)
        {
          try
          {
            if (indexColumn.isNullable())
            {
              nullableColumnsInUniqueIndex.add(index);
              break;
            }
          }
          catch (final NotLoadedException e)
          {
            // The column may be partial for index pseudo-columns
            continue;
          }
        }
      }
    }
    return nullableColumnsInUniqueIndex;
  }

}
