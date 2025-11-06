/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnPrivilegesRetrievalStrategy;
import static us.fatehi.utility.ioresource.PropertiesMap.fromProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
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
import us.fatehi.test.utility.TestUtility;
import us.fatehi.utility.datasource.ConnectionDatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public final class DatabaseTestUtility {

  public static final SchemaRetrievalOptions schemaRetrievalOptionsDefault =
      SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();
  public static final SchemaCrawlerOptions schemaCrawlerOptionsWithMaximumSchemaInfoLevel =
      getMaximumSchemaCrawlerOptions();

  public static Catalog getCatalog(
      final Connection connection, final SchemaCrawlerOptions schemaCrawlerOptions) {
    return getCatalog(connection, schemaRetrievalOptionsDefault, schemaCrawlerOptions);
  }

  public static Catalog getCatalog(
      final Connection connection,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final SchemaCrawlerOptions schemaCrawlerOptions) {

    final DatabaseConnectionSource dataSource = new ConnectionDatabaseConnectionSource(connection);

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(dataSource, schemaRetrievalOptions, schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();
    return catalog;
  }

  public static Map<String, String> loadHsqldbConfig() throws IOException {
    final Properties properties =
        TestUtility.loadPropertiesFromClasspath("/hsqldb.INFORMATION_SCHEMA.config.properties");
    return fromProperties(properties).toMap();
  }

  public static Path tempHsqldbConfig() throws IOException {
    final Properties properties =
        TestUtility.loadPropertiesFromClasspath("/hsqldb.INFORMATION_SCHEMA.config.properties");
    return TestUtility.savePropertiesToTempFile(properties);
  }

  private static SchemaCrawlerOptions getMaximumSchemaCrawlerOptions() {
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeAllTables()
            .includeAllRoutines()
            .includeAllSequences()
            .includeAllSynonyms();
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLoadOptions(loadOptionsBuilder.toOptions())
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  private DatabaseTestUtility() {
    // Prevent instantiation
  }

  public static void validateSchema(final Catalog catalog) {
    assertThat("Could not obtain catalog", catalog, notNullValue());

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat(
        "Unexpected number of tables in the schema", catalog.getColumnDataTypes(), hasSize(32));
    assertThat("Unexpected number of tables in the schema", catalog.getTables(schema), hasSize(11));
    assertThat(
        "Unexpected number of routines in the schema", catalog.getRoutines(schema), hasSize(5));
    assertThat(
        "Unexpected number of synonyms in the schema", catalog.getSynonyms(schema), hasSize(0));
    assertThat(
        "Unexpected number of sequences in the schema", catalog.getSequences(schema), hasSize(0));
  }

  public static SchemaRetrievalOptions newSchemaRetrievalOptions() throws IOException {
    final Map<String, String> config = loadHsqldbConfig();

    final InformationSchemaViewsBuilder builder = InformationSchemaViewsBuilder.builder();

    for (final InformationSchemaKey informationSchemaKey : InformationSchemaKey.values()) {
      final String lookupKey =
          "select.%s.%s".formatted(informationSchemaKey.getType(), informationSchemaKey);
      if (config.containsKey(lookupKey)) {
        try {
          builder.withSql(informationSchemaKey, config.get(lookupKey));
        } catch (final IllegalArgumentException e) {
          // Ignore
        }
      }
    }
    final InformationSchemaViews informationSchemaViews = builder.toOptions();

    return SchemaRetrievalOptionsBuilder.builder()
        .withInformationSchemaViews(informationSchemaViews)
        .with(tableColumnPrivilegesRetrievalStrategy, data_dictionary_all)
        .toOptions();
  }
}
