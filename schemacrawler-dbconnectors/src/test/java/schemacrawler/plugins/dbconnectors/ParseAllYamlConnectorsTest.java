/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.CaptureSystemStreams;
import us.fatehi.test.utility.extensions.CapturedSystemStreams;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.utility.ioresource.ClasspathInputResource;

@ResolveTestContext
@CaptureSystemStreams
class ParseAllYamlConnectorsTest {

  private static final String PARSED_DBCONNECTORS_OUTPUT = "parsed_dbconnectors_output/";

  private final DatabasePluginYamlDeserializer deserializer = new DatabasePluginYamlDeserializer();

  @ParameterizedTest
  @ValueSource(
      strings = {"access", "cassandra", "clickhouse", "duckdb", "h2", "snowflake", "trino"})
  void parseYamlConnectors(final String server, final CapturedSystemStreams streams)
      throws IOException {

    final DatabaseConnectorDefinition definition =
        deserializer.parse(new ClasspathInputResource("dbconnectors/%s.yaml".formatted(server)));

    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    record DatabaseConnectorDefinitionHolder(DatabaseConnectorDefinition databaseConnector) {}

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      DatabasePluginYamlDeserializer.mapper.writeValue(
          out, new DatabaseConnectorDefinitionHolder(definition));
    }

    assertThat(outputOf(streams.err()), hasNoContent());
    assertThat(definition.databaseServerType().server(), is(server));

    assertThat(
        outputOf(testout.getFilePath()),
        hasSameContentAs(classpathResource(PARSED_DBCONNECTORS_OUTPUT + server + ".yaml")));
  }
}
