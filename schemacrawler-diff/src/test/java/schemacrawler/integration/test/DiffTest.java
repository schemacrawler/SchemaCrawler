/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.DiffNode.State;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.integration.objectdiffer.SchemaCrawlerDifferBuilder;
import schemacrawler.utility.NamedObjectSort;
import schemacrawler.utility.SchemaCrawlerUtility;


public class DiffTest
{

  @Rule
  public TestName testName = new TestName();
  @Rule
  public EmbeddedDatabaseRule testdb1Rule = EmbeddedDatabaseRule.builder()
    .withName("testdb1").withInitialSqlFromResource("classpath:testdb1.sql")
    .build();
  @Rule
  public EmbeddedDatabaseRule testdb2Rule = EmbeddedDatabaseRule.builder()
    .withName("testdb2").withInitialSqlFromResource("classpath:testdb2.sql")
    .build();

  @Test
  @Ignore
  public void diffCatalog()
    throws Exception
  {
    final Catalog catalog1 = getCatalog(testdb1Rule.getConnection());
    final Catalog catalog2 = getCatalog(testdb2Rule.getConnection());

    final String currentMethodFullName = testName.currentMethodFullName();

    final SchemaCrawlerDifferBuilder objectDifferBuilder = new SchemaCrawlerDifferBuilder();

    try (final TestWriter out = new TestWriter("text");)
    {
      final DiffNode diff = objectDifferBuilder.build().compare(catalog1,
                                                                catalog2);
      diff.visit((node, visit) -> {
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
      });

      out.assertEquals(currentMethodFullName);
    }
  }

  @Test
  @Ignore
  public void printColumns1()
    throws Exception
  {
    printColumns(testName.currentMethodFullName(), testdb1Rule.getConnection());
  }

  @Test
  @Ignore
  public void printColumns2()
    throws Exception
  {
    printColumns(testName.currentMethodFullName(), testdb2Rule.getConnection());
  }

  private Catalog getCatalog(final Connection connection)
    throws Exception
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final Catalog catalog = SchemaCrawlerUtility
      .getCatalog(connection, schemaCrawlerOptions);

    return catalog;
  }

  private void printColumns(final String currentMethodFullName,
                            final Connection connection)
    throws Exception
  {

    try (final TestWriter out = new TestWriter("text");)
    {
      final Catalog baseCatalog = getCatalog(connection);
      final CatalogWithAssociations catalog = new CatalogWithAssociations(baseCatalog);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertEquals("Schema count does not match", 2, schemas.length);
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

      out.assertEquals(currentMethodFullName);
    }
  }

}
