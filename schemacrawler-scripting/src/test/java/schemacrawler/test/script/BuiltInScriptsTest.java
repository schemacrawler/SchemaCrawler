/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.ScriptTestUtility.scriptExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@ResolveTestContext
@WithTestDatabase
@EnabledOnOs(
    value = {OS.WINDOWS},
    architectures = {"x64", "x86_64", "amd64"},
    disabledReason = "Does not run on Windows ARM")
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
