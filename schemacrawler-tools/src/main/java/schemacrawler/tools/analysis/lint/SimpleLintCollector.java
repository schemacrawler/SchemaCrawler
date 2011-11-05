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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Table;

public class SimpleLintCollector
  implements LintCollector
{

  public static Lint<?>[] getLint(final NamedObject namedObject)
  {
    if (namedObject == null)
    {
      return null;
    }

    final Collection<Lint<?>> lints = namedObject
      .getAttribute(LINT_KEY, new ArrayList<Lint<?>>());
    final Lint<?>[] objectLints = lints.toArray(new Lint<?>[lints.size()]);
    Arrays.sort(objectLints);
    return objectLints;
  }

  private final Collection<Lint<?>> lints;

  public SimpleLintCollector()
  {
    lints = new ArrayList<Lint<?>>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#addLint(schemacrawler.schema.Column,
   *      schemacrawler.tools.analysis.lint.Lint)
   */
  @Override
  public void addLint(final Column column, final Lint<?> lint)
  {
    addNamedObjectLint(column, lint);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#addLint(schemacrawler.schema.Database,
   *      schemacrawler.tools.analysis.lint.Lint)
   */
  @Override
  public void addLint(final Database database, final Lint<?> lint)
  {
    addNamedObjectLint(database, lint);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.analysis.lint.LintCollector#addLint(schemacrawler.schema.Table,
   *      schemacrawler.tools.analysis.lint.Lint)
   */
  @Override
  public void addLint(final Table table, final Lint<?> lint)
  {
    addNamedObjectLint(table, lint);
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
  public Iterator<Lint<?>> iterator()
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
  public Lint<?>[] toArray()
  {
    return lints.toArray(new Lint<?>[lints.size()]);
  }

  private void addNamedObjectLint(final NamedObject namedObject,
                                  final Lint<?> lint)
  {
    if (namedObject != null && lint != null
        && namedObject.getFullName().equals(lint.getObjectName()))
    {
      lints.add(lint);

      final Collection<Lint<?>> columnLints = namedObject
        .getAttribute(LINT_KEY, new ArrayList<Lint<?>>());
      columnLints.add(lint);
      namedObject.setAttribute(LINT_KEY, columnLints);
    }
  }

}
