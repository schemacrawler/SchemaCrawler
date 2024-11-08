/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.utility;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static us.fatehi.utility.Utility.convertForComparison;

import java.util.Comparator;
import java.util.Objects;

import schemacrawler.schema.NamedObject;

public enum NamedObjectSort implements Comparator<NamedObject> {

  /** Alphabetical sort, case-insensitive. */
  alphabetical(comparing(namedObject -> convertForComparison(namedObject.getFullName()))),

  /** Natural sort. */
  natural(naturalOrder());

  public static NamedObjectSort getNamedObjectSort(final boolean alphabeticalSort) {
    if (alphabeticalSort) {
      return NamedObjectSort.alphabetical;
    } else {
      return NamedObjectSort.natural;
    }
  }

  private final Comparator<NamedObject> comparator;

  NamedObjectSort(final Comparator<NamedObject> comparator) {
    this.comparator = nullsLast(comparator);
  }

  /** {@inheritDoc} */
  @Override
  public int compare(final NamedObject namedObject1, final NamedObject namedObject2) {
    return Objects.compare(namedObject1, namedObject2, comparator);
  }
}
