/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;

import static java.util.Objects.hash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import schemacrawler.schema.BaseForeignKey;
import schemacrawler.schema.NamedObject;
import us.fatehi.utility.CompareUtility;

/** Represents a foreign-key mapping to a primary key in another table. */
abstract class AbstractForeignKey<R extends schemacrawler.schema.ColumnReference>
    extends AbstractNamedObjectWithAttributes implements BaseForeignKey<R> {

  private static final long serialVersionUID = -5164664131926303038L;

  private final SortedSet<R> columnReferences;

  public AbstractForeignKey(final String name) {
    super(name);
    columnReferences = new TreeSet<>();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Note: Since foreign keys are not always explicitly named in databases, the sorting routine
   * orders the foreign keys by the names of the columns in the foreign keys.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    if (obj == null) {
      return -1;
    }

    final BaseForeignKey<?> other = (BaseForeignKey<?>) obj;
    final List<R> thisColumnReferences = getColumnReferences();
    final List<? extends schemacrawler.schema.ColumnReference> otherColumnReferences =
        other.getColumnReferences();

    return CompareUtility.compareLists(thisColumnReferences, otherColumnReferences);
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BaseForeignKey)) {
      return false;
    }
    final BaseForeignKey<?> other = (BaseForeignKey<?>) obj;
    return Objects.equals(getColumnReferences(), other.getColumnReferences());
  }

  @Override
  public List<R> getColumnReferences() {
    return new ArrayList<>(columnReferences);
  }

  @Override
  public final int hashCode() {
    return hash(columnReferences);
  }

  @Override
  public Iterator<R> iterator() {
    return columnReferences.iterator();
  }

  void addColumnReference(final R columnReference) {
    if (columnReference != null) {
      columnReferences.add(columnReference);
    }
  }
}
