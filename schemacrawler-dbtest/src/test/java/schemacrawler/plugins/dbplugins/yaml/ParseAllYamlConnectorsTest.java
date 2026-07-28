/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.yaml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import schemacrawler.plugins.dbplugins.model.DatabaseConnectorDefinition;
import us.fatehi.utility.ioresource.ClasspathInputResource;

class ParseAllYamlConnectorsTest {

  private final DatabasePluginYamlDeserializer deserializer = new DatabasePluginYamlDeserializer();

  @ParameterizedTest
  @ValueSource(
      strings = {"access", "cassandra", "clickhouse", "duckdb", "h2", "snowflake", "trino"})
  void parseYamlConnectors(final String connector) throws IOException {

    final DatabaseConnectorDefinition definition =
        deserializer.parse(
            new ClasspathInputResource("schemacrawler-dbplugins/%s.yaml".formatted(connector)));

    assertThat(definition.databaseServerType().server(), is(connector));
  }
}
