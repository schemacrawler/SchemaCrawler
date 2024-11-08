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

package schemacrawler.test.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.ScriptTestUtility.scriptExecution;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithSystemProperty;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@ResolveTestContext
@WithTestDatabase
public class DiagramScriptTest {

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
