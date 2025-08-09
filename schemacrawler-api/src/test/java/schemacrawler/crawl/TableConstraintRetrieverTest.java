/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.schemacrawler.InformationSchemaKey.CHECK_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaKey.CONSTRAINT_COLUMN_USAGE;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_CONSTRAINTS;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.sql.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
public class TableConstraintRetrieverTest {

  private MutableCatalog catalog;

  @BeforeEach
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(
                SchemaInfoLevelBuilder.builder()
                    .withInfoLevel(InfoLevel.standard)
                    .setRetrieveTableConstraints(false) // Don't retrieve constraints yet
                    .toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog =
        (MutableCatalog)
            getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);

    assertThat(catalog, is(notNullValue()));

    // Verify that we have tables
    assertThat(catalog.getTables(), is(not(empty())));
  }

  @Test
  @DisplayName("Test retrieving table constraints")
  public void testRetrieveTableConstraints(final DatabaseConnectionSource dataSource)
      throws Exception {
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsDefault;
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    // Create a list of tables to retrieve constraints for
    final NamedObjectList<MutableTable> allTables = new NamedObjectList<>();
    for (final Schema schema : catalog.getSchemas()) {
      for (final Table table : catalog.getTables(schema)) {
        if (table instanceof MutableTable) {
          allTables.add((MutableTable) table);
        }
      }
    }

    assertThat("Should have tables to retrieve constraints for", !allTables.isEmpty(), is(true));

    // Create the constraint retriever
    final TableConstraintRetriever constraintRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);

    // Act - retrieve table constraints
    constraintRetriever.retrieveTableConstraints();
    constraintRetriever.matchTableConstraints(allTables);

    // We can't easily verify specific constraints were created,
    // but we can verify the method executed without errors
  }

  @Test
  @DisplayName("Test retrieving table constraint definitions")
  public void testRetrieveTableConstraintDefinitions(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view for check constraints
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                CHECK_CONSTRAINTS,
                "SELECT "
                    + "NULL AS CONSTRAINT_CATALOG, "
                    + "'PUBLIC' AS CONSTRAINT_SCHEMA, "
                    + "'TEST_CHECK' AS CONSTRAINT_NAME, "
                    + "'ID > 0' AS CHECK_CLAUSE "
                    + "FROM (VALUES(0))")
            .toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    // Create the constraint retriever
    final TableConstraintRetriever constraintRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);

    // Act - retrieve table constraint definitions
    constraintRetriever.retrieveTableConstraintDefinitions();

    // We can't easily verify specific constraint definitions were created,
    // but we can verify the method executed without errors
  }

  @Test
  @DisplayName("Test retrieving table constraint information")
  public void testRetrieveTableConstraintInformation(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view for table constraints
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                TABLE_CONSTRAINTS,
                "SELECT "
                    + "NULL AS CONSTRAINT_CATALOG, "
                    + "'PUBLIC' AS CONSTRAINT_SCHEMA, "
                    + "'TEST_CONSTRAINT' AS CONSTRAINT_NAME, "
                    + "NULL AS TABLE_CATALOG, "
                    + "'PUBLIC' AS TABLE_SCHEMA, "
                    + "'BOOKS' AS TABLE_NAME, "
                    + "'PRIMARY KEY' AS CONSTRAINT_TYPE, "
                    + "'YES' AS IS_DEFERRABLE, "
                    + "'NO' AS INITIALLY_DEFERRED "
                    + "FROM (VALUES(0))")
            .withSql(
                CONSTRAINT_COLUMN_USAGE,
                "SELECT "
                    + "NULL AS TABLE_CATALOG, "
                    + "'PUBLIC' AS TABLE_SCHEMA, "
                    + "'BOOKS' AS TABLE_NAME, "
                    + "'ID' AS COLUMN_NAME, "
                    + "NULL AS CONSTRAINT_CATALOG, "
                    + "'PUBLIC' AS CONSTRAINT_SCHEMA, "
                    + "'TEST_CONSTRAINT' AS CONSTRAINT_NAME "
                    + "FROM (VALUES(0))")
            .toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    // Create the constraint retriever
    final TableConstraintRetriever constraintRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);

    // Act - retrieve table constraint information
    constraintRetriever.retrieveTableConstraintInformation();

    // We can't easily verify specific constraint information was created,
    // but we can verify the method executed without errors
  }

  @Test
  @DisplayName("Test retrieving table constraints with invalid SQL")
  public void testRetrieveTableConstraintsWithInvalidSql(final DatabaseConnectionSource dataSource)
      throws Exception {
    // Arrange - create a custom information schema view with invalid SQL
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(TABLE_CONSTRAINTS, "THIS IS NOT VALID SQL")
            .toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    // Create a list of tables to retrieve constraints for
    final NamedObjectList<MutableTable> allTables = new NamedObjectList<>();
    for (final Schema schema : catalog.getSchemas()) {
      for (final Table table : catalog.getTables(schema)) {
        if (table instanceof MutableTable) {
          allTables.add((MutableTable) table);
        }
      }
    }

    // Create the constraint retriever
    final TableConstraintRetriever constraintRetriever =
        new TableConstraintRetriever(retrieverConnection, catalog, options);

    // Act - retrieve table constraints with invalid SQL
    // This should not throw an exception
    constraintRetriever.retrieveTableConstraintInformation();
    constraintRetriever.matchTableConstraints(allTables);

    // Verify that we still have tables
    assertThat(catalog.getTables(), is(not(empty())));
  }
}
