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
import static schemacrawler.test.utility.ScriptTestUtility.commandLineScriptExecution;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.WithTestDatabase;

@AssertNoSystemOutOutput
@WithTestDatabase
public class CommandlineScriptCommandTest {

  @Test
  public void commandlineJavaScript(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(commandLineScriptExecution(connectionInfo, "/plaintextschema.js")),
        hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  public void commandlinePython(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(commandLineScriptExecution(connectionInfo, "/plaintextschema.py")),
        hasSameContentAs(classpathResource("script_output.txt")));
  }

  @Test
  @Disabled("Needs Ruby installed in GraalVM")
  public void commandlineRuby(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(commandLineScriptExecution(connectionInfo, "/plaintextschema.rb")),
        hasSameContentAs(classpathResource("script_output_rb.txt")));
  }
}
