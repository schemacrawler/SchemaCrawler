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

package schemacrawler.tools.lint.report;

import static schemacrawler.tools.lint.LintUtility.LINT_COMPARATOR;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.Options;
import schemacrawler.tools.lint.Lint;
import us.fatehi.utility.Multimap;

public final class LintReport implements Options, Iterable<Lint<? extends Serializable>> {

  private final String title;
  private final CrawlInfo crawlInfo;
  private final List<Lint<? extends Serializable>> allLints;
  private final Multimap<NamedObjectKey, Lint<?>> lintsByObject;

  LintReport(
      final String title,
      final CrawlInfo crawlInfo,
      final List<Lint<? extends Serializable>> lints) {

    this.title = trimToEmpty(title);
    this.crawlInfo = crawlInfo; // Can be null

    requireNonNull(lints, "No lints provided");
    allLints = new ArrayList<>(lints);
    allLints.sort(LINT_COMPARATOR);

    lintsByObject = new Multimap<>();
    for (final Lint<?> lint : lints) {
      lintsByObject.add(lint.getObjectKey(), lint);
    }
  }

  /**
   * Gets information about when the catalog was crawled.
   *
   * @return Catalog crawl information.
   */
  public CrawlInfo getCrawlInfo() {
    return crawlInfo;
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
   * Get all lints for a given named object identified by its key, sorted in natural sorting order.
   *
   * @return All lints for a named object.
   */
  public List<Lint<?>> getLints(final NamedObject namedObject) {
    requireNonNull(namedObject, "No named object provided");

    final NamedObjectKey key = namedObject.key();
    final List<Lint<? extends Serializable>> lintsForKey = lintsByObject.get(key);
    if (lintsForKey == null) {
      return Collections.emptyList();
    }

    final List<Lint<?>> lints = new ArrayList<>(lintsForKey);
    lints.sort(LINT_COMPARATOR);
    return lints;
  }

  /**
   * Gets the lint report title.
   *
   * @return Lint report title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Whether crawl information is available.
   *
   * @return True if crawl information is available.
   */
  public boolean hasCrawlInfo() {
    return crawlInfo != null;
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
}
