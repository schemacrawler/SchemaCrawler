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

package schemacrawler.tools.lint;

import static schemacrawler.tools.lint.LintUtility.LINT_COMPARATOR;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Options;
import us.fatehi.utility.Multimap;

/** Immutable collection of lints, with lookup methods useful for reporting. */
// Contrast with the internal lint collector, which is mutable, and shared
// between linters.
public final class Lints implements Options, Iterable<Lint<? extends Serializable>> {

  private final List<Lint<? extends Serializable>> allLints;
  private final Multimap<NamedObjectKey, Lint<?>> lintsByObject;

  public Lints(final Collection<Lint<? extends Serializable>> lints) {

    requireNonNull(lints, "No lints provided");
    allLints = new ArrayList<>(lints);
    allLints.sort(LINT_COMPARATOR);

    lintsByObject = new Multimap<>();
    for (final Lint<?> lint : lints) {
      lintsByObject.add(lint.getObjectKey(), lint);
    }
  }

  /**
   * Get all lints for the catalog, sorted in natural sorting order.
   *
   * @return All lints for a named object.
   */
  public List<Lint<?>> getCatalogLints() {
    return getLints(new NamedObjectKey("catalog"));
  }

  /**
   * Get all lints, sorted in natural sorting order.
   *
   * @return All lints in the report.
   */
  public List<Lint<? extends Serializable>> getLints() {
    return new ArrayList<>(allLints);
  }

  /**
   * Get all lints for a given table, sorted in natural sorting order.
   *
   * @return All lints for a named object.
   */
  public List<Lint<?>> getLints(final Table table) {
    requireNonNull(table, "No table provided");
    return getLints(table.key());
  }

  /**
   * Whether there are any lints in the report.
   *
   * @return True if lint report is empty.
   */
  public boolean isEmpty() {
    return allLints.isEmpty();
  }

  @Override
  public Iterator<Lint<? extends Serializable>> iterator() {
    return getLints().iterator();
  }

  /**
   * Number of lints in the report.
   *
   * @return Number of lints.
   */
  public int size() {
    return allLints.size();
  }

  /**
   * Stream of lints in the report.
   *
   * @return Stream of lints.
   */
  public Stream<Lint<? extends Serializable>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  private List<Lint<?>> getLints(final NamedObjectKey key) {
    final List<Lint<? extends Serializable>> lintsForKey = lintsByObject.get(key);
    if (lintsForKey == null) {
      return Collections.emptyList();
    }

    final List<Lint<?>> lints = new ArrayList<>(lintsForKey);
    lints.sort(LINT_COMPARATOR);
    return lints;
  }
}
