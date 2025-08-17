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
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.metadata;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.proceduresRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.sql.Connection;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineType;
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
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@ResolveTestContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoutineExtRetrieverTest {

  private MutableCatalog catalog;

  @BeforeAll
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(
                SchemaInfoLevelBuilder.builder()
                    .withInfoLevel(InfoLevel.minimum)
                    .setRetrieveRoutines(false)
                    .toOptions());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());
    catalog = (MutableCatalog) getCatalog(connection, schemaCrawlerOptions);

    assertThat(catalog.getRoutines(), is(empty()));
  }

  @Test
  @DisplayName("Retrieve procedures references")
  public void routineReferences(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.ROUTINE_REFERENCES,
                "SELECT "
                    + "  'PUBLIC' AS ROUTINE_CATALOG,"
                    + " 'BOOKS' AS ROUTINE_SCHEMA,"
                    + "  'NEW_PUBLISHER' AS ROUTINE_NAME,"
                    + "  'NEW_PUBLISHER_FORCE_VALUE' AS SPECIFIC_NAME,"
                    + "  'PUBLIC' AS REFERENCED_OBJECT_CATALOG,"
                    + "  'BOOKS' AS REFERENCED_OBJECT_SCHEMA,"
                    + "  'AUTHORSLIST' AS REFERENCED_OBJECT_NAME,"
                    + "  NULL AS REFERENCED_OBJECT_SPECIFIC_NAME,"
                    + "  'VIEW' AS REFERENCED_OBJECT_TYPE"
                    + " FROM INFORMATION_SCHEMA.SYSTEM_TABLES"
                    + " WHERE 1=1"
                    + " LIMIT 1")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(proceduresRetrievalStrategy, metadata)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    // Retrieve routines first
    final RoutineRetriever routineRetriever =
        new RoutineRetriever(retrieverConnection, catalog, options);
    routineRetriever.retrieveRoutines(
        Arrays.asList(RoutineType.procedure, RoutineType.function), new IncludeAll());

    final RoutineExtRetriever procedureExtRetriever =
        new RoutineExtRetriever(retrieverConnection, catalog, options);
    procedureExtRetriever.retrieveRoutineReferences();

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Routine[] procedures = catalog.getRoutines().toArray(new Routine[0]);
      Arrays.sort(procedures, NamedObjectSort.alphabetical);
      for (final Routine procedure : procedures) {
        out.println(
            String.format(
                "%s (%s) [%s]",
                procedure.getFullName(), procedure.getSpecificName(), procedure.getRoutineType()));
        out.println(String.format(" - references: %s", procedure.getReferencedObjects()));
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
