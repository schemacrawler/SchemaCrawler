/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ScriptTestUtility.scriptExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@WithTestDatabase
@EnabledOnOs(
    value = {OS.WINDOWS},
    architectures = {"x64", "x86_64", "amd64"},
    disabledReason = "Does not run on Windows ARM")
public class ExecutableScriptCommandTest {

  @Test
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
  @Disabled("Needs Ruby installed in GraalVM")
  public void executableRuby(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(scriptExecution(dataSource, "/plaintextschema.rb")),
        hasSameContentAs(classpathResource("script_output_rb.txt")));
  }
}
