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

package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.ALL;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.TABLES;
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
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseObjectListFunctionTest {

  private Catalog catalog;

  @Test
  public void all(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(ALL);
    databaseObjects(testContext, args);
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

  @Test
  public void parameters(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(ALL);
    assertThat(args.toString(), is("{\"database-object-type\":\"ALL\"}"));
  }

  @Test
  public void routines(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(ROUTINES);
    databaseObjects(testContext, args);
  }

  @Test
  public void sequences(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(SEQUENCES);
    databaseObjects(testContext, args);
  }

  @Test
  public void synonyms(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(SYNONYMS);
    databaseObjects(testContext, args);
  }

  @Test
  public void tables(final TestContext testContext) throws Exception {
    final DatabaseObjectListFunctionParameters args = new DatabaseObjectListFunctionParameters();
    args.setDatabaseObjectType(TABLES);
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
      out.write(functionReturn.get());
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
