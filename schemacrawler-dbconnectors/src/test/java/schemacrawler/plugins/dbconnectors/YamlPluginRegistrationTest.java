/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

/** Verifies that each YAML-defined plugin is registered correctly and carries no SQL overrides. */
public class YamlPluginRegistrationTest {

  @ParameterizedTest
  @ValueSource(
      strings = {"access", "cassandra", "clickhouse", "duckdb", "h2", "snowflake", "trino"})
  void pluginIsRegistered(final String server) {
    final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
    assertThat(registry.hasDatabaseSystemIdentifier(server), is(true));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"access", "cassandra", "clickhouse", "duckdb", "h2", "snowflake", "trino"})
  void informationSchemaViewsAreEmpty(final String server) throws Exception {
    final DatabaseConnectorRegistry registry = DatabaseConnectorRegistry.getRegistry();
    final DatabaseConnector connector = registry.getDatabaseConnector(server);
    assertThat(
        connector
            .getSchemaRetrievalOptionsBuilder(null)
            .toOptions()
            .getInformationSchemaViews()
            .size(),
        is(0));
  }
}
