/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
