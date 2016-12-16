/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.utility.MetaDataUtility.findForeignKeyCardinality;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.analysis.associations.CatalogWithAssociations;
import schemacrawler.tools.analysis.associations.WeakAssociation;
import schemacrawler.tools.analysis.associations.WeakAssociationForeignKey;
import schemacrawler.tools.analysis.associations.WeakAssociationsUtility;
import schemacrawler.tools.sqlite.SQLiteDatabaseConnector;
import schemacrawler.utility.NamedObjectSort;
import schemacrawler.utility.SchemaCrawlerUtility;

public class PrimaryKeyWeakAssociationsTest
  extends BaseDatabaseTest
{

  @Rule
  public TestName testName = new TestName();

  @Test
  public void weakAssociations1()
    throws Exception
  {
    weakAssociations(testName.currentMethodFullName(), "/pk_test_1.db");
  }

  @Test
  public void weakAssociations2()
    throws Exception
  {
    weakAssociations(testName.currentMethodFullName(), "/pk_test_2.db");
  }

  @Test
  public void weakAssociations3()
    throws Exception
  {
    weakAssociations(testName.currentMethodFullName(), "/pk_test_3.db");
  }

  private void weakAssociations(final String currentMethodFullName,
                                final String database)
    throws Exception
  {
    final Path sqliteDbFile = copyResourceToTempFile(database);
    final Config config = new Config();
    config.put("server", "sqlite");
    config.put("database", sqliteDbFile.toString());

    try (final TestWriter out = new TestWriter("text");)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

      final ConnectionOptions connectionOptions = new SQLiteDatabaseConnector()
        .newDatabaseConnectionOptions(config);

      final Catalog baseCatalog = SchemaCrawlerUtility
        .getCatalog(connectionOptions.getConnection(), schemaCrawlerOptions);
      final CatalogWithAssociations catalog = new CatalogWithAssociations(baseCatalog);
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertEquals("Schema count does not match", 1, schemas.length);
      for (final Schema schema: schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table: tables)
        {
          out.println("  table: " + table.getFullName());
          final Collection<WeakAssociationForeignKey> weakAssociations = WeakAssociationsUtility
            .getWeakAssociations(table);
          for (final WeakAssociationForeignKey weakFk: weakAssociations)
          {
            out.println(String.format("    weak association (1 to %s):",
                                      findForeignKeyCardinality(weakFk)));
            for (final WeakAssociation weakAssociation: weakFk)
            {
              out.println(String.format("      column reference: %s",
                                        weakAssociation));
            }
          }
        }
      }

      out.assertEquals(currentMethodFullName);
    }
  }

}
