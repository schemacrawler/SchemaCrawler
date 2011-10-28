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
package schemacrawler.tools.analysis.lint;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Table;

public class SimpleLintCollector
  implements LintCollector
{

  private final Set<Lint> lints;

  public SimpleLintCollector()
  {
    lints = new HashSet<Lint>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#addLint(schemacrawler.schema.Column,
   *      schemacrawler.tools.analysis.lint.Lint)
   */
  @Override
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#addLint(schemacrawler.schema.Database,
   *      schemacrawler.tools.analysis.lint.Lint)
   */
  @Override
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#addLint(schemacrawler.schema.Table,
   *      schemacrawler.tools.analysis.lint.Lint)
   */
  @Override
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#clear()
   */
  @Override
  public void clear()
  {
    lints.clear();
  }

  @Override
  public Lint[] getLint(final Column column)
  {
    if (column == null)
    {
      return new Lint[0];
    }
    final String fullName = column.getFullName();

    return getLintForObject(fullName);
  }

  @Override
  public Lint[] getLint(final Database database)
  {
    return getLintForObject(null);
  }

  @Override
  public Lint[] getLint(final Table table)
  {
    if (table == null)
    {
      return new Lint[0];
    }
    final String fullName = table.getFullName();

    return getLintForObject(fullName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return lints.isEmpty();
  }

  @Override
  public Iterator<Lint> iterator()
  {
    return lints.iterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#size()
   */
  @Override
  public int size()
  {
    return lints.size();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#toArray()
   */
  @Override
  public Lint[] toArray()
  {
    return lints.toArray(new Lint[lints.size()]);
  }

  private Lint[] getLintForObject(final String fullName)
  {
    final Set<Lint> objectLints = new HashSet<Lint>();
    for (final Lint lint: lints)
    {
      if (fullName.equals(lint.getObjectName()))
      {
        objectLints.add(lint);
      }
    }
    return objectLints.toArray(new Lint[objectLints.size()]);
  }

}
