/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.crawl;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.utility.NamedObjectSort;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForeignKeyRetrieverTest
{

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve foreign keys from data dictionary")
  public void fkFromDataDictionary(final Connection connection)
    throws Exception
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
      .withForeignKeyRetrievalStrategy(MetadataRetrievalStrategy.data_dictionary_all)
      .withInformationSchemaViewsBuilder()
      .withSql(InformationSchemaKey.FOREIGN_KEYS, "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_CROSSREFERENCE");
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection = new RetrieverConnection(connection, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final ForeignKeyRetriever foreignKeyRetriever = new ForeignKeyRetriever(retrieverConnection, catalog, options);
    foreignKeyRetriever.retrieveForeignKeys(catalog.getAllTables());

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout)
    {
      final Schema[] schemas = catalog
        .getSchemas()
        .toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(5));
      for (final Schema schema : schemas)
      {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog
          .getTables(schema)
          .toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables)
        {
          out.println("  table: " + table.getFullName());
          final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
          for (final ForeignKey foreignKey : foreignKeys)
          {
            out.println("    foreign key: " + foreignKey.getName());
            out.println("      specific name: " + foreignKey.getSpecificName());
            out.println("      deferrability: " + foreignKey.getDeferrability());
            out.println("      delete rule: " + foreignKey.getDeleteRule());
            out.println("      update rule: " + foreignKey.getUpdateRule());

            out.println("      column references: ");
            final List<ForeignKeyColumnReference> columnReferences = foreignKey.getColumnReferences();
            for (final ForeignKeyColumnReference columnReference : columnReferences)
            {
              out.println("        key sequence: " + columnReference.getKeySequence());
              out.println("          " + columnReference);
            }
          }
        }
      }
    }
    // IMPORTANT: The data dictionary should return the same information as the metadata test
    assertThat(outputOf(testout), hasSameContentAs(classpathResource("SchemaCrawlerTest.foreignKeys")));
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection)
    throws SchemaCrawlerException
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = SchemaCrawlerOptionsBuilder
      .builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder
                             .builder()
                             .withInfoLevel(InfoLevel.standard)
                             .setRetrieveForeignKeys(false)
                             .setRetrieveForeignKeyDefinitions(false)
                             .toOptions())
      .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"))
      .toOptions();
    catalog = (MutableCatalog) getCatalog(connection,
                                          SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions(),
                                          schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(13));
    for (final Table table : tables)
    {
      assertThat(table.getColumns(), is(not(empty())));
      assertThat(table.getForeignKeys(), is(empty()));
    }
  }

}
