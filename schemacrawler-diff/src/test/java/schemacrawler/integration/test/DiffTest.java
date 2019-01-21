/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.DiffNode.State;
import de.danielbechler.diff.node.Visit;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.databaseconnector.ConnectionOptions;
import schemacrawler.tools.integration.objectdiffer.SchemaCrawlerDifferBuilder;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;
import schemacrawler.utility.NamedObjectSort;
import schemacrawler.utility.SchemaCrawlerUtility;

@ExtendWith(TestContextParameterResolver.class)
public class DiffTest
{

  @Test
  public void diffCatalog(final TestContext testContext)
    throws Exception
  {
    final Catalog catalog1 = getCatalog("/test1.db");
    final Catalog catalog2 = getCatalog("/test2.db");

    final String currentMethodFullName = testContext.testMethodFullName();

    final SchemaCrawlerDifferBuilder objectDifferBuilder = new SchemaCrawlerDifferBuilder();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final DiffNode diff = objectDifferBuilder.build().compare(catalog1,
                                                                catalog2);
      diff.visit(new DiffNode.Visitor()
      {
        @Override
        public void node(final DiffNode node, final Visit visit)
        {
          final State nodeState = node.getState();
          final boolean print = DatabaseObject.class
            .isAssignableFrom(node.getValueType());

          if (print)
          {
            out.println(node.getPath() + " (" + nodeState + ")");
          }

          if (Table.class.isAssignableFrom(node.getValueType())
              && nodeState != State.CHANGED)
          {
            visit.dontGoDeeper();
          }
          if (Column.class.isAssignableFrom(node.getValueType()))
          {
            visit.dontGoDeeper();
          }
        }
      });
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(currentMethodFullName)));
  }

  @Test
  public void printColumns1(final TestContext testContext)
    throws Exception
  {
    printColumns(testContext, "/test1.db");
  }

  @Test
  public void printColumns2(final TestContext testContext)
    throws Exception
  {
    printColumns(testContext, "/test2.db");
  }

  private Catalog getCatalog(final String database)
    throws Exception
  {
    final Path sqliteDbFile = copyResourceToTempFile(database);
    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", sqliteDbFile.toString());

    final SchemaCrawlerOptions schemaCrawlerOptions = DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final ConnectionOptions connectionOptions = new SQLiteDatabaseConnector()
      .newDatabaseConnectionOptions(new SingleUseUserCredentials(), config);

    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(connectionOptions.getConnection(), schemaCrawlerOptions);

    return catalog;
  }

  private void printColumns(final TestContext testContext,
                            final String database)
    throws Exception
  {
    final String currentMethodFullName = testContext.testMethodFullName();
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Catalog baseCatalog = getCatalog(database);
      final CatalogWithAssociations catalog = new CatalogWithAssociations(baseCatalog);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, is(arrayWithSize(1)));
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          for (final Column column: table.getColumns())
          {
            out.println(String.format("      column: %s", column));
          }
        }
      }
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(currentMethodFullName)));
  }

}
