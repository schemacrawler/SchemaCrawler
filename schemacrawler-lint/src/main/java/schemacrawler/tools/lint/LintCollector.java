/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.naturalOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;

public final class LintCollector {

  private static final String LINT_KEY = "schemacrawler.lint";

  private final List<Lint<? extends Serializable>> lints;

  public LintCollector() {
    lints = new ArrayList<>();
  }

  public <N extends NamedObject & AttributedObject> void addLint(
      final N namedObject, final Lint<?> lint) {
    if (namedObject != null
        && lint != null
        && namedObject.getFullName().equals(lint.getObjectName())) {
      lints.add(lint);

      final Collection<Lint<?>> columnLints = namedObject.getAttribute(LINT_KEY, new ArrayList<>());
      columnLints.add(lint);
      namedObject.setAttribute(LINT_KEY, columnLints);
    }
  }

  public Collection<Lint<? extends Serializable>> getLints() {
    lints.sort(naturalOrder());
    return new ArrayList<>(lints);
  }

  public int size() {
    return lints.size();
  }
}
