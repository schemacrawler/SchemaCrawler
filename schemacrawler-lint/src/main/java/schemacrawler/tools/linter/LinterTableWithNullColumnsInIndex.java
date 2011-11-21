/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;

public class LinterTableWithNullColumnsInIndex
  extends BaseLinter
{

  @Override
  public String getDescription()
  {
    return getSummary();
  }

  @Override
  public String getSummary()
  {
    return "unique index with nullable columns";
  }

  @Override
  public void lint(final Table table)
  {
    if (table != null)
    {
      final List<Index> nullableColumnsInUniqueIndex = findNullableColumnsInUniqueIndex(table
        .getIndices());
      for (final Index index: nullableColumnsInUniqueIndex)
      {
        addLint(table, getSummary(), index);
      }
    }
  }

  private List<Index> findNullableColumnsInUniqueIndex(final Index[] indices)
  {
    final List<Index> nullableColumnsInUniqueIndex = new ArrayList<Index>();
    for (final Index index: indices)
    {
      if (index.isUnique())
      {
        final IndexColumn[] indexColumns = index.getColumns();
        for (final IndexColumn indexColumn: indexColumns)
        {
          if (indexColumn.isNullable())
          {
            nullableColumnsInUniqueIndex.add(index);
            break;
          }
        }
      }
    }
    return nullableColumnsInUniqueIndex;
  }

}
