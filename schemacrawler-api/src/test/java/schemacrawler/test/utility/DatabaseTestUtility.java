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

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceUtility;

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

    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource(connection);

    final SchemaCrawler schemaCrawler =
        new SchemaCrawler(dataSource, schemaRetrievalOptions, schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();
    return catalog;
  }

  public static Map<String, String> loadHsqldbConfig() throws IOException {
    final Properties properties =
        TestUtility.loadPropertiesFromClasspath("/hsqldb.INFORMATION_SCHEMA.config.properties");
    return PropertiesUtility.propertiesMap(properties);
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
}
