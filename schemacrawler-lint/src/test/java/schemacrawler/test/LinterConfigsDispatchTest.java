/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintOptionsBuilder;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.config.LinterConfig;
import schemacrawler.tools.lint.config.LinterConfigs;

@WithTestDatabase
@ResolveTestContext
public class LinterConfigsDispatchTest {

  @Test
  @AssertNoSystemErrOutput
  @AssertNoSystemOutOutput
  public void linterConfigs(final TestContext testContext) throws IOException {

    final LintOptions lintOptions =
        LintOptionsBuilder.builder()
            .withLinterConfigs("/schemacrawler-linter-configs-with-dispatch.yaml")
            .toOptions();

    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions);

    assertThat(linterConfigs.size(), is(1));
    boolean asserted = false;
    for (final LinterConfig linterConfig : linterConfigs) {
      if (linterConfig == null) {
        fail("Null linter config");
      }
      if ("schemacrawler.tools.linter.LinterTableWithNoPrimaryKey"
          .equals(linterConfig.getLinterId())) {
        assertThat(linterConfig.getSeverity(), is(LintSeverity.critical));
        assertThat(linterConfig.getThreshold(), is(2));
        assertThat(linterConfig.isRunLinter(), is(true));
        asserted = true;
      }
    }
    if (!asserted) {
      fail();
    }

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.write(linterConfigs.toString());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
