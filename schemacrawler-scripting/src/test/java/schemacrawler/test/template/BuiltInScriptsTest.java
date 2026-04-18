/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ScriptTestUtility.templateExecution;
import static schemacrawler.tools.command.template.options.TemplateLanguageType.velocity;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@ResolveTestContext
@WithTestDatabase
@DisableLogging
public class BuiltInScriptsTest {

  @Test
  public void dbml(final TestContext testContext, final DatabaseConnectionSource connectionSource)
      throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/dbml.vm")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  public void markdown(
      final TestContext testContext, final DatabaseConnectionSource connectionSource)
      throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/markdown.vm")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  public void mermaid(
      final TestContext testContext, final DatabaseConnectionSource connectionSource)
      throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/mermaid.vm")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  public void plantuml(
      final TestContext testContext, final DatabaseConnectionSource connectionSource)
      throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/plantuml.vm")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }
}
