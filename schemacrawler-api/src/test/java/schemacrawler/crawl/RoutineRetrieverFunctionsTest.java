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
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
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
import schemacrawler.schema.Catalog;
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
public class RoutineRetrieverFunctionsTest {

  private MutableCatalog catalog;

  @Test
  @DisplayName("Retrieve functions from data dictionary")
  public void functionsFromDataDictionary(
      final TestContext testContext, final DatabaseConnectionSource dataSource) throws Exception {
    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder()
            .withSql(
                InformationSchemaKey.FUNCTIONS,
                "SELECT "
                    + "PROCEDURE_CAT AS FUNCTION_CAT, PROCEDURE_SCHEM AS FUNCTION_SCHEM, "
                    + "PROCEDURE_NAME AS FUNCTION_NAME, PROCEDURE_TYPE AS FUNCTION_TYPE, "
                    + "REMARKS, SPECIFIC_NAME "
                    + "FROM INFORMATION_SCHEMA.SYSTEM_PROCEDURES")
            .toOptions();
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();
    schemaRetrievalOptionsBuilder
        .with(functionsRetrievalStrategy, data_dictionary_all)
        .withInformationSchemaViews(informationSchemaViews);
    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();
    final RetrieverConnection retrieverConnection =
        new RetrieverConnection(dataSource, schemaRetrievalOptions);

    final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();

    final RoutineRetriever functionRetriever =
        new RoutineRetriever(retrieverConnection, catalog, options);
    functionRetriever.retrieveRoutines(Arrays.asList(RoutineType.function), new IncludeAll());

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final Routine[] functions = ((Catalog) catalog).getRoutines().toArray(new Routine[0]);
      Arrays.sort(functions, NamedObjectSort.alphabetical);
      for (final Routine function : functions) {
        out.println(
            String.format(
                "%s (%s) [%s]",
                function.getFullName(), function.getSpecificName(), function.getRoutineType()));
      }
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

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
    catalog =
        (MutableCatalog)
            getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);

    assertThat(catalog.getRoutines(), is(empty()));
  }
}
