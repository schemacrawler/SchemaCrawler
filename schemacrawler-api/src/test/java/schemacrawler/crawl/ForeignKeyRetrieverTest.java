/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ForeignKeyRetrieverTest {

  public static void verifyRetrieveForeignKeys(final Catalog catalog) throws IOException {
    verifyRetrieveForeignKeys(catalog, "SchemaCrawlerTest.foreignKeys");
  }

  public static void verifyRetrieveForeignKeys(final Catalog catalog, final String referenceFile)
      throws IOException {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Schema[] schemas = catalog.getSchemas().toArray(new Schema[0]);
      assertThat("Schema count does not match", schemas, arrayWithSize(greaterThan(2)));
      for (final Schema schema : schemas) {
        out.println("schema: " + schema.getFullName());
        final Table[] tables = catalog.getTables(schema).toArray(new Table[0]);
        Arrays.sort(tables, NamedObjectSort.alphabetical);
        for (final Table table : tables) {
          out.println("  table: " + table.getFullName());
          final Collection<ForeignKey> foreignKeys = table.getForeignKeys();
          for (final ForeignKey foreignKey : foreignKeys) {
            out.println("    foreign key: " + foreignKey.getName());
            out.println("      definition: " + foreignKey.getDefinition());
            out.println("      deferrability: " + foreignKey.getDeferrability());
            out.println("      initially deferred: " + foreignKey.isInitiallyDeferred());
            out.println("      delete rule: " + foreignKey.getDeleteRule());
            out.println("      update rule: " + foreignKey.getUpdateRule());

            out.println("      dependent table: " + foreignKey.getDependentTable());
            out.println("      referenced table: " + foreignKey.getReferencedTable());
            out.println("      column references: ");
            final List<ColumnReference> columnReferences = foreignKey.getColumnReferences();
            for (final ColumnReference columnReference : columnReferences) {
              out.println("        key sequence: " + columnReference.getKeySequence());
              out.println("          " + columnReference);
            }
            out.println("      table constraint: ");
            out.println("        parent (dependent table): " + foreignKey.getParent());
            for (final TableConstraintColumn constraintColumn :
                foreignKey.getConstrainedColumns()) {
              out.println("          constrained column: " + constraintColumn);
            }

            assertThat(foreignKey.isDeferrable(), is(foreignKey.isInitiallyDeferred()));
          }
        }
      }
    }
    // IMPORTANT: The data dictionary test should return the same information as the metadata test
    assertThat(outputOf(testout), hasSameContentAs(classpathResource(referenceFile)));
  }

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve foreign keys from data dictionary")
  public void fkFromDataDictionary(final DatabaseConnectionSource dataSource) throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.FOREIGN_KEYS,
                "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_CROSSREFERENCE")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(foreignKeysRetrievalStrategy, data_dictionary_all)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final ForeignKeyRetriever foreignKeyRetriever =
        new ForeignKeyRetriever(retrieverConnection, catalog, options);
    foreignKeyRetriever.retrieveForeignKeys(catalog.getAllTables());

    verifyRetrieveForeignKeys(catalog);
  }

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(
                SchemaInfoLevelBuilder.builder()
                    .withInfoLevel(InfoLevel.standard)
                    .setRetrieveForeignKeys(false)
                    .toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    catalog = (MutableCatalog) getCatalog(connection, schemaCrawlerOptions);

    final Collection<Table> tables = catalog.getTables();
    assertThat(tables, hasSize(14));
    for (final Table table : tables) {
      assertThat(table.getColumns(), is(not(empty())));
      assertThat(table.getForeignKeys(), is(empty()));
    }
  }
}
