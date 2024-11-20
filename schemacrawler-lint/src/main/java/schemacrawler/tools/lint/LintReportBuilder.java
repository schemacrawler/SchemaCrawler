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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static us.fatehi.utility.Utility.trimToEmpty;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.Builder;

/** SchemaCrawler lint report builder, to build lint report. */
public final class LintReportBuilder implements Builder<LintReport> {

  public static LintReportBuilder builder() {
    return new LintReportBuilder();
  }

  private String title;
  private CrawlInfo crawlInfo;
  private List<Lint<? extends Serializable>> allLints;

  /** Default options. */
  private LintReportBuilder() {
    title = "";
    crawlInfo = null;
    allLints = new ArrayList<>();
  }

  @Override
  public LintReport build() {
    return new LintReport(title, crawlInfo, allLints);
  }

  public LintReportBuilder fromCatalog(final Catalog catalog) {
    if (catalog != null) {
      crawlInfo = catalog.getCrawlInfo();
    }
    return this;
  }

  public LintReportBuilder withCrawlInfo(final CrawlInfo crawlInfo) {
    if (crawlInfo != null) {
      this.crawlInfo = crawlInfo;
    }
    return this;
  }

  public LintReportBuilder withLintCollector(final LintCollector lintCollector) {
    if (lintCollector != null) {
      allLints = lintCollector.getLints();
    }
    return this;
  }

  public LintReportBuilder withLinters(final Linters linters) {
    if (linters != null) {
      return withLintCollector(linters.getCollector());
    }
    return this;
  }

  public LintReportBuilder withLints(final List<Lint<? extends Serializable>> lints) {
    if (lints != null) {
      allLints = lints;
    }
    return this;
  }

  public LintReportBuilder withOutputOptions(final OutputOptions outputOptions) {
    if (outputOptions != null) {
      title = outputOptions.getTitle();
    }
    return this;
  }

  public LintReportBuilder withTitle(final String title) {
    this.title = trimToEmpty(title);
    return this;
  }
}
