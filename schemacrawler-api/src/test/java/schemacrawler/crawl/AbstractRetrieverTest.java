/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.InformationSchemaKey;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

/** An abstract base class for retriever tests to reduce code duplication. */
@WithTestDatabase
public abstract class AbstractRetrieverTest {

  protected MutableCatalog catalog;

  /**
   * Loads a base catalog before each test.
   *
   * @param connection Database connection
   */
  @BeforeEach
  public void loadBaseCatalog(final Connection connection) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));

    // Create SchemaInfoLevelBuilder with base settings
    final SchemaInfoLevelBuilder schemaInfoLevelBuilder =
        SchemaInfoLevelBuilder.builder().withInfoLevel(getInfoLevel());

    // Allow subclasses to customize the SchemaInfoLevelBuilder
    customizeSchemaInfoLevel(schemaInfoLevelBuilder);

    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(schemaInfoLevelBuilder.toOptions());

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog = (MutableCatalog) getCatalog(connection, schemaCrawlerOptions);

    assertThat(catalog, is(notNullValue()));
  }

  /**
   * Customize the SchemaInfoLevelBuilder for specific retriever tests. Subclasses should override
   * this method to disable specific object retrieval.
   *
   * @param schemaInfoLevelBuilder SchemaInfoLevelBuilder to customize
   */
  protected void customizeSchemaInfoLevel(final SchemaInfoLevelBuilder schemaInfoLevelBuilder) {
    // Default implementation does nothing
  }

  /**
   * Creates a RetrieverConnection with custom information schema views.
   *
   * @param dataSource Database connection source
   * @param informationSchemaKey Information schema key
   * @param sql SQL query for the information schema view
   * @param retrievalStrategy Metadata retrieval strategy
   * @return RetrieverConnection
   * @throws SQLException If a database access error occurs
   */
  protected RetrieverConnection createRetrieverConnection(
      final DatabaseConnectionSource dataSource,
      final InformationSchemaKey informationSchemaKey,
      final String sql,
      final MetadataRetrievalStrategy retrievalStrategy)
      throws SQLException {

    final InformationSchemaViews informationSchemaViews =
        InformationSchemaViewsBuilder.builder().withSql(informationSchemaKey, sql).toOptions();

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
        SchemaRetrievalOptionsBuilder.builder();

    if (retrievalStrategy != null) {
      schemaRetrievalOptionsBuilder
          .with(getRetrievalStrategyKey(), retrievalStrategy)
          .withInformationSchemaViews(informationSchemaViews);
    } else {
      schemaRetrievalOptionsBuilder.withInformationSchemaViews(informationSchemaViews);
    }

    final SchemaRetrievalOptions schemaRetrievalOptions = schemaRetrievalOptionsBuilder.toOptions();

    return new RetrieverConnection(dataSource, schemaRetrievalOptions);
  }

  /**
   * Creates a RetrieverConnection with default schema retrieval options.
   *
   * @param dataSource Database connection source
   * @return RetrieverConnection
   * @throws SQLException If a database access error occurs
   */
  protected RetrieverConnection createRetrieverConnection(final DatabaseConnectionSource dataSource)
      throws SQLException {
    return new RetrieverConnection(dataSource, schemaRetrievalOptionsDefault);
  }

  /**
   * Creates default SchemaCrawlerOptions.
   *
   * @return SchemaCrawlerOptions
   */
  protected SchemaCrawlerOptions createOptions() {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  /**
   * Gets the info level for the test. Override this method to change the info level.
   *
   * @return InfoLevel
   */
  protected InfoLevel getInfoLevel() {
    return InfoLevel.minimum;
  }

  /**
   * Gets the retrieval strategy key for the test. Override this method to specify the retrieval
   * strategy key.
   *
   * @return Retrieval strategy key
   */
  protected abstract SchemaInfoMetadataRetrievalStrategy getRetrievalStrategyKey();
}
