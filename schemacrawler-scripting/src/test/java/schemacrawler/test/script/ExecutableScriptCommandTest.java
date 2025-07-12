/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.condition.OS.WINDOWS;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.ScriptTestUtility.scriptExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@WithTestDatabase
public class ExecutableScriptCommandTest {

  @Test
  public void executableGroovy(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/plaintextschema.groovy")),
        hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  @DisabledOnOs(
      value = WINDOWS,
      disabledReason = "Graal JS has a bug with Unicode output on Windows")
  public void executableJavaScript(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/plaintextschema.js")),
        hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  @WithSystemProperty(key = "python.console.encoding", value = "UTF-8")
  public void executablePython(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/plaintextschema.py")),
        hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void executableRuby(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/plaintextschema.rb")),
        hasSameContentAs(classpathResource("script_output_rb.txt")));
  }
}
