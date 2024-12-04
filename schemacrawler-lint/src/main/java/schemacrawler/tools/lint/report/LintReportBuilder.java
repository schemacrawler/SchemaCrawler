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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.lint.Lint;

/** SchemaCrawler lint report builder, to build lint report. */
public final class LintReportBuilder implements OptionsBuilder<LintReportBuilder, LintReport> {

  public static LintReportBuilder builder() {
    return new LintReportBuilder();
  }

  private CrawlInfo crawlInfo;
  private List<Lint<? extends Serializable>> allLints;

  /** Default options. */
  private LintReportBuilder() {
    crawlInfo = null;
    allLints = new ArrayList<>();
  }

  @Override
  public LintReportBuilder fromOptions(final LintReport lintReport) {
    if (lintReport != null) {
      crawlInfo = lintReport.getCrawlInfo();
      allLints = lintReport.getLints();
    }
    return this;
  }

  @Override
  public LintReport toOptions() {
    return new LintReport(crawlInfo, allLints);
  }

  public LintReportBuilder withCatalog(final Catalog catalog) {
    if (catalog != null) {
      withCrawlInfo(catalog.getCrawlInfo());
    }
    return this;
  }

  public LintReportBuilder withCrawlInfo(final CrawlInfo crawlInfo) {
    if (crawlInfo != null) {
      this.crawlInfo = crawlInfo;
    }
    return this;
  }

  public LintReportBuilder withLints(final Collection<Lint<? extends Serializable>> lints) {
    if (lints != null) {
      allLints = new ArrayList<>(lints);
    }
    return this;
  }
}
