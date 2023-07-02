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
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType;
import schemacrawler.tools.command.chatgpt.functions.FunctionReturn;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseObjectFunctionTest {

  private Catalog catalog;

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

  @Test
  public void routines(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(DatabaseObjectType.ROUTINES);
    databaseObjects(testContext, args);
  }

  @Test
  public void schemas(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(DatabaseObjectType.SCHEMAS);
    databaseObjects(testContext, args);
  }

  @Test
  public void sequences(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(DatabaseObjectType.SEQUENCES);
    databaseObjects(testContext, args);
  }

  @Test
  public void synonyms(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(DatabaseObjectType.SYNONYMS);
    databaseObjects(testContext, args);
  }

  @Test
  public void tables(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(DatabaseObjectType.TABLES);
    databaseObjects(testContext, args);
  }

  private void databaseObjects(
      final TestContext testContext, final DatabaseObjectListFunctionParameters args)
      throws Exception {

    final DatabaseObjectListFunctionDefinition functionDefinition =
        new DatabaseObjectListFunctionDefinition();
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
