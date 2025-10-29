/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ScriptTestUtility.scriptExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@ResolveTestContext
@WithTestDatabase
@EnabledOnOs(
    value = {OS.WINDOWS},
    architectures = {"x64", "x86_64", "amd64"},
    disabledReason = "Does not run on Windows ARM")
@DisableLogging
public class BuiltInScriptsTest {

  @Test
  @WithSystemProperty(key = "python.console.encoding", value = "UTF-8")
  public void dbml(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/scripts/dbml.py")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  @WithSystemProperty(key = "python.console.encoding", value = "UTF-8")
  public void markdown(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/scripts/markdown.py")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  @WithSystemProperty(key = "python.console.encoding", value = "UTF-8")
  public void mermaid(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/scripts/mermaid.py")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }

  @Test
  @WithSystemProperty(key = "python.console.encoding", value = "UTF-8")
  public void plantuml(final TestContext testContext, final DatabaseConnectionSource dataSource)
      throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/scripts/plantuml.py")),
        hasSameContentAs(classpathResource(testContext.testMethodFullName() + ".txt")));
  }
}
