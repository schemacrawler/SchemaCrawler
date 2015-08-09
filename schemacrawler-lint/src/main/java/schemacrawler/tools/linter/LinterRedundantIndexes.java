/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
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
package schemacrawler.tools.linter;


import static schemacrawler.tools.lint.LintUtility.listStartsWith;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.tools.lint.BaseLinterTable;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.utility.MetaDataUtility;

public class LinterRedundantIndexes
  extends BaseLinterTable
{

  public LinterRedundantIndexes()
  {
    setSeverity(LintSeverity.high);
  }

  @Override
  public String getSummary()
  {
    return "redundant index";
  }

  @Override
  protected void lint(final Table table)
  {
    if (table != null && !(table instanceof View))
    {
      final Collection<Index> indexes = table.getIndexes();
      if (indexes.size() > 0)
      {
        final Set<Index> redundantIndexes = findRedundantIndexes(indexes);
        for (final Index index: redundantIndexes)
        {
          addLint(table, getSummary(), index);
        }
      }
    }
  }

  private Set<Index> findRedundantIndexes(final Collection<Index> indexes)
  {
    final Set<Index> redundantIndexes = new HashSet<>();
    final Map<Index, List<String>> indexColumns = new HashMap<>(indexes.size());
    for (final Index index: indexes)
    {
      indexColumns.put(index, MetaDataUtility.columnNames(index));
    }

    for (final Entry<Index, List<String>> indexColumnEntry1: indexColumns
      .entrySet())
    {
      for (final Entry<Index, List<String>> indexColumnEntry2: indexColumns
        .entrySet())
      {
        if (!indexColumnEntry1.equals(indexColumnEntry2))
        {
          if (listStartsWith(indexColumnEntry1.getValue(),
                             indexColumnEntry2.getValue()))
          {
            redundantIndexes.add(indexColumnEntry2.getKey());
          }
        }
      }
    }
    return redundantIndexes;
  }

}
