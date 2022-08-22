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
package schemacrawler.test.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.utility.MetaDataUtility.findForeignKeyCardinality;

import java.util.Arrays;
import java.util.Collection;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class ProposedWeakAssociationsTestUtility {

  public static void weakAssociations(
      final TestContext testContext,
      final DatabaseConnectionSource dataSource,
      final boolean inferExtensionTables)
      throws Exception {

    final String currentMethodFullName;
    if (inferExtensionTables) {
      currentMethodFullName = testContext.testMethodFullName() + ".infer-extension-tables";
    } else {
      currentMethodFullName = testContext.testMethodFullName();
    }

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final SchemaCrawlerOptions schemaCrawlerOptions =
          DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

      final SchemaRetrievalOptions schemaRetrievalOptions =
          SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();

      final Config config = new Config();
      config.put("weak-associations", Boolean.TRUE);
      config.put("infer-extension-tables", Boolean.valueOf(inferExtensionTables));

      final Catalog catalog =
          SchemaCrawlerUtility.getCatalog(
              dataSource, schemaRetrievalOptions, schemaCrawlerOptions, config);

      final Schema schema = new SchemaReference("PUBLIC", "PUBLIC");
      final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
      Arrays.sort(tables, NamedObjectSort.alphabetical);
      for (final Table table : tables) {
        out.println("table: " + table.getFullName());
        final Collection<WeakAssociation> weakAssociations = table.getWeakAssociations();
        for (final WeakAssociation weakFk : weakAssociations) {
          out.println(
              String.format("  weak association (1 to %s):", findForeignKeyCardinality(weakFk)));
          for (final ColumnReference weakAssociationColumnReference : weakFk) {
            out.println(String.format("    column reference: %s", weakAssociationColumnReference));
          }
        }
      }
    }
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(currentMethodFullName)));
  }

  private ProposedWeakAssociationsTestUtility() {
    // Prevent instantiation
  }
}
