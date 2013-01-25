/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;

public class LintUtility
{

  public static final List<String> columns(final Index index)
  {
    if (index == null)
    {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<String>();
    for (final IndexColumn indexColumn: index.getColumns())
    {
      columnNames.add(indexColumn.getFullName());
    }
    return columnNames;
  }

  public static final List<String> foreignKeyColumns(final ForeignKey foreignKey)
  {
    if (foreignKey == null)
    {
      return Collections.emptyList();
    }

    final List<String> columnNames = new ArrayList<String>();
    for (final ForeignKeyColumnReference columnReference: foreignKey
      .getColumnReferences())
    {
      columnNames.add(columnReference.getForeignKeyColumn().getFullName());
    }
    return columnNames;
  }

  public static final <E> boolean listStartsWith(final List<E> main,
                                                 final List<E> sub)
  {
    if (main == null || sub == null)
    {
      return false;
    }
    if (main.size() < sub.size())
    {
      return false;
    }
    if (main.isEmpty())
    {
      return true;
    }

    return main.subList(0, sub.size()).equals(sub);

  }

  private LintUtility()
  {
  }

}
