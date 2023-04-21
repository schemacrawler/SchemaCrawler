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

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import schemacrawler.schema.CrawlInfo;

public final class LintReport implements Iterable<Lint<? extends Serializable>> {

  private final CrawlInfo crawlInfo;
  private final Collection<Lint<? extends Serializable>> lints;
  private final String title;

  public LintReport(
      final String title,
      final CrawlInfo crawlInfo,
      final Collection<Lint<? extends Serializable>> lints) {
    this.title = trimToEmpty(title);
    this.crawlInfo = requireNonNull(crawlInfo, "No crawl information provided");
    this.lints = requireNonNull(lints, "No lints provided");
  }

  public CrawlInfo getCrawlInfo() {
    return crawlInfo;
  }

  public Collection<Lint<? extends Serializable>> getLints() {
    return new ArrayList<>(lints);
  }

  public String getTitle() {
    return title;
  }

  @Override
  public Iterator<Lint<? extends Serializable>> iterator() {
    return getLints().iterator();
  }

  public int size() {
    return lints.size();
  }
}
