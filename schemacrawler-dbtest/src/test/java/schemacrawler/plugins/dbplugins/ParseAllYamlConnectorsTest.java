/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import schemacrawler.plugins.dbplugins.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbplugins.yaml.DatabasePluginYamlDeserializer;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.utility.ioresource.ClasspathInputResource;

@ResolveTestContext
@CaptureSystemStreams
class ParseAllYamlConnectorsTest {

  private final DatabasePluginYamlDeserializer deserializer = new DatabasePluginYamlDeserializer();

  @ParameterizedTest
  @ValueSource(
      strings = {"access", "cassandra", "clickhouse", "duckdb", "h2", "snowflake", "trino"})
  void parseYamlConnectors(final String server, final CapturedSystemStreams streams)
      throws IOException {

    final DatabaseConnectorDefinition definition =
        deserializer.parse(
            new ClasspathInputResource("schemacrawler-dbplugins/%s.yaml".formatted(server)));

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(definition.databaseServerType().server(), is(server));
  }
}
