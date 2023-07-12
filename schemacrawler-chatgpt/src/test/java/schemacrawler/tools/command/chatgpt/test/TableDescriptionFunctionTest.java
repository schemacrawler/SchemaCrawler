/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.chatgpt.functions.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableDescriptionFunctionTest {

  private Catalog catalog;

  @Test
  public void describeTable(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("AUTHORS");
    describeTable(testContext, args);
  }

  @Test
  public void describeTableColumns(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("PUBLISHERS");
    args.setDescriptionScope(TableDescriptionScope.COLUMNS);
    describeTable(testContext, args);
  }

  @Test
  public void describeTableForeignKeys(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("BOOKAUTHORS");
    args.setDescriptionScope(TableDescriptionScope.FOREIGN_KEYS);
    describeTable(testContext, args);
  }

  @Test
  public void describeTableIndexes(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("BOOKAUTHORS");
    args.setDescriptionScope(TableDescriptionScope.INDEXES);
    describeTable(testContext, args);
  }

  @Test
  public void describeTableLike(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("Celeb");
    describeTable(testContext, args);
  }

  @Test
  public void describeTablePrimaryKey(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("AUTHORS");
    args.setDescriptionScope(TableDescriptionScope.PRIMARY_KEY);
    describeTable(testContext, args);
  }

  @Test
  public void describeTableTriggers(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("AUTHORS");
    args.setDescriptionScope(TableDescriptionScope.TRIGGERS);
    describeTable(testContext, args);
  }

  @Test
  public void describeUnknownTable(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("NOT_A_TABLE");
    describeTable(testContext, args);
  }

  @Test
  public void describeView(final TestContext testContext) throws Exception {
    final TableDecriptionFunctionParameters args = new TableDecriptionFunctionParameters();
    args.setTableNameContains("AuthorsList");
    describeTable(testContext, args);
  }

  @BeforeAll
  public void loadCatalog(final Connection connection) throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
            .includeAllSynonyms()
            .includeAllSequences()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog = getCatalog(connection, schemaRetrievalOptions, schemaCrawlerOptions);
  }

  private void describeTable(
      final TestContext testContext, final TableDecriptionFunctionParameters args)
      throws Exception {

    final TableDecriptionFunctionDefinition functionDefinition =
        new TableDecriptionFunctionDefinition();
    functionDefinition.setCatalog(catalog);

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final FunctionReturn functionReturn = functionDefinition.getExecutor().apply(args);
      out.write(functionReturn.render());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
