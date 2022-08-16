/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
