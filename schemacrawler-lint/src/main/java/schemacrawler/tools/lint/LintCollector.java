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
package schemacrawler.tools.lint;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;

public final class LintCollector
  implements Iterable<Lint<? extends Serializable>>
{

  private static final String LINT_KEY = "schemacrawler.lint";

  public static Collection<Lint<?>> getLint(final AttributedObject namedObject)
  {
    if (namedObject == null)
    {
      return null;
    }

    final List<Lint<? extends Serializable>> lints = new ArrayList<>(namedObject
      .getAttribute(LINT_KEY, new ArrayList<>()));
    Collections.sort(lints);
    return lints;
  }

  private final List<Lint<? extends Serializable>> lints;

  public LintCollector()
  {
    lints = new ArrayList<>();
  }

  public <N extends NamedObject & AttributedObject> void addLint(final N namedObject,
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

  @Override
  public Iterator<Lint<? extends Serializable>> iterator()
  {
    Collections.sort(lints);
    return lints.iterator();
  }

  public int size()
  {
    return lints.size();
  }

}
