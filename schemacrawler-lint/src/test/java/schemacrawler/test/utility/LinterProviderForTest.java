/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.utility;

import java.sql.Connection;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderForTest extends BaseLinterProvider {

  private static final long serialVersionUID = 7775205295917734672L;

  public LinterProviderForTest() {
    super(LinterForTest.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterForTest(getPropertyName(), lintCollector);
  }
}

class LinterForTest extends BaseLinter {

  LinterForTest(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.low);
  }

  @Override
  public String getSummary() {
    return "Test catalog linter";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    // No-op, since the actual linting is done in the start method
  }

  @Override
  protected void start(final Connection connection) {
    addCatalogLint("Test lint for the whole catalog, without a value");
    final CrawlInfo crawlInfo = getCrawlInfo();
    addCatalogLint("Test lint for database", crawlInfo.getDatabaseVersion());
  }
}
