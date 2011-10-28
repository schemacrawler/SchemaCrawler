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
package schemacrawler.tools.analysis;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Table;

public class LintCollector
  implements Iterable<Lint>
{

  private final Set<Lint> lints;

  public LintCollector()
  {
    lints = new HashSet<Lint>();
  }

  public void addLint(final Column column, final Lint lint)
  {
    if (lint != null)
    {
      lints.add(lint);

      final Collection<Lint> columnLints = column
        .getAttribute(Lint.LINT_KEY, new HashSet<Lint>());
      columnLints.add(lint);
      column.setAttribute(Lint.LINT_KEY, columnLints);
    }
  }

  public void addLint(final Database database, final Lint lint)
  {
    if (lint != null)
    {
      lints.add(lint);

      final Collection<Lint> dbLints = database
        .getAttribute(Lint.LINT_KEY, new HashSet<Lint>());
      dbLints.add(lint);
      database.setAttribute(Lint.LINT_KEY, dbLints);
    }
  }

  public void addLint(final Table table, final Lint lint)
  {
    if (lint != null)
    {
      lints.add(lint);

      final Collection<Lint> tableLints = table
        .getAttribute(Lint.LINT_KEY, new HashSet<Lint>());
      tableLints.add(lint);
      table.setAttribute(Lint.LINT_KEY, tableLints);
    }
  }

  public void clear()
  {
    lints.clear();
  }

  public boolean isEmpty()
  {
    return lints.isEmpty();
  }

  @Override
  public Iterator<Lint> iterator()
  {
    return lints.iterator();
  }

  public int size()
  {
    return lints.size();
  }

  public Lint[] toArray()
  {
    return lints.toArray(new Lint[lints.size()]);
  }

}
