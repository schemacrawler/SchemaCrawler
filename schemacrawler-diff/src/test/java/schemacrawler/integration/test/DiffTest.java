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

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.DiffNode.State;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.objectdiffer.SchemaCrawlerDifferBuilder;
import schemacrawler.tools.sqlite.EmbeddedSQLiteWrapper;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

@DisableLogging
@ResolveTestContext
public class DiffTest {

  @Test
  public void diffCatalog(final TestContext testContext) throws Exception {
    final Catalog catalog1 = getCatalog("/test1.db");
    final Catalog catalog2 = getCatalog("/test2.db");

    final String currentMethodFullName = testContext.testMethodFullName();

    final SchemaCrawlerDifferBuilder objectDifferBuilder = new SchemaCrawlerDifferBuilder();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final DiffNode diff = objectDifferBuilder.build().compare(catalog1, catalog2);
      diff.visit(
          (node, visit) -> {
            final State nodeState = node.getState();
            final boolean print = DatabaseObject.class.isAssignableFrom(node.getValueType());

            if (print) {
              out.println(node.getPath() + " (" + nodeState + ")");
            }

            if (Table.class.isAssignableFrom(node.getValueType()) && nodeState != State.CHANGED) {
              visit.dontGoDeeper();
            }
            if (Column.class.isAssignableFrom(node.getValueType())) {
              visit.dontGoDeeper();
            }
          });
    }
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(currentMethodFullName)));
  }

  @Test
  public void printSchema1(final TestContext testContext) throws Exception {
    printSchema(testContext, "/test1.db");
  }

  @Test
  public void printSchema2(final TestContext testContext) throws Exception {
    printSchema(testContext, "/test2.db");
  }

  private DatabaseConnectionSource createDataSource(final Path sqliteDbFile) {
    final String connectionUrl = "jdbc:sqlite:" + sqliteDbFile;
    return DatabaseConnectionSources.newDatabaseConnectionSource(
        connectionUrl, new MultiUseUserCredentials());
  }

  private Catalog getCatalog(final String database) throws Exception {
    final Path sqliteDbFile = copyResourceToTempFile(database);

    final EmbeddedSQLiteWrapper sqLiteDatabaseLoader = new EmbeddedSQLiteWrapper();
    sqLiteDatabaseLoader.setDatabasePath(sqliteDbFile);

    final SchemaCrawlerOptions schemaCrawlerOptions =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final DatabaseConnectionSource dataSource =
        sqLiteDatabaseLoader.createDatabaseConnectionSource();

    final Catalog catalog = SchemaCrawlerUtility.getCatalog(dataSource, schemaCrawlerOptions);

    return catalog;
  }

  private void printSchema(final TestContext testContext, final String database) throws Exception {
    final String currentMethodFullName = testContext.testMethodFullName();
    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("schema");
    executable.setSchemaCrawlerOptions(SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

    final Path sqliteDbFile = TestUtility.copyResourceToTempFile(database);
    final DatabaseConnectionSource dataSource = createDataSource(sqliteDbFile);
    assertThat(
        outputOf(executableExecution(dataSource, executable)),
        hasSameContentAs(classpathResource(currentMethodFullName)));
  }
}
