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

package schemacrawler.integration.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

@DisableLogging
@ExtendWith(TestContextParameterResolver.class)
public class VirtualTablesTest extends BaseSqliteTest {

  @Test
  public void count(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), InfoLevel.minimum, "count", new IncludeAll());
  }

  @Test
  public void list(final TestContext testContext) throws Exception {
    run(testContext.testMethodFullName(), InfoLevel.minimum, "list", new IncludeAll());
  }

  @Test
  public void schema(final TestContext testContext) throws Exception {
    final ExecutionRuntimeException exception =
        assertThrows(
            ExecutionRuntimeException.class,
            () ->
                run(
                    testContext.testMethodFullName(),
                    InfoLevel.standard,
                    "schema",
                    new IncludeAll()));
    assertThat(
        exception.getMessage(),
        is(
            "Could not retrieve table columns for table <demo>: [SQLITE_ERROR] SQL error or missing database (no such module: spellfix1)"));
  }

  @Test
  public void schemaNonVirtual(final TestContext testContext) throws Exception {
    run(
        testContext.testMethodFullName(),
        InfoLevel.standard,
        "schema",
        new RegularExpressionExclusionRule("demo.*"));
  }

  private void run(
      final String currentMethodFullName,
      final InfoLevel infoLevel,
      final String command,
      final InclusionRule tableInclusionRule)
      throws Exception {
    final DataSource dataSource = createDataSourceFromResource("with_spellfix1_tables.db");

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder().includeTables(tableInclusionRule);
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions())
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    assertThat(
        outputOf(executableExecution(dataSource.getConnection(), executable)),
        hasSameContentAs(classpathResource(currentMethodFullName)));
  }
}
