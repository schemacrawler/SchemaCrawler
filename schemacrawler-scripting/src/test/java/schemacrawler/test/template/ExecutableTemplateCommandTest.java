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

package schemacrawler.test.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.ScriptTestUtility.templateExecution;

import org.junit.jupiter.api.Test;

import schemacrawler.test.utility.AssertNoSystemErrOutput;
import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class ExecutableTemplateCommandTest {

  @Test
  public void executableFreeMarker(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(
            templateExecution(dataSource, TemplateLanguageType.freemarker, "/plaintextschema.ftl")),
        hasSameContentAs(classpathResource("executableForFreeMarker.txt")));
  }

  @Test
  public void executableMustache(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(
            templateExecution(
                dataSource, TemplateLanguageType.mustache, "/plaintextschema.mustache")),
        hasSameContentAs(classpathResource("executableForMustache.txt")));
  }

  @Test
  public void executableThymeleaf(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(
            templateExecution(
                dataSource, TemplateLanguageType.thymeleaf, "/plaintextschema.thymeleaf")),
        hasSameContentAs(classpathResource("executableForThymeleaf.txt")));
  }

  @Test
  public void executableVelocity(final DatabaseConnectionSource dataSource) throws Exception {
    assertThat(
        outputOf(
            templateExecution(dataSource, TemplateLanguageType.velocity, "/plaintextschema.vm")),
        hasSameContentAs(classpathResource("executableForVelocity.txt")));
  }
}
