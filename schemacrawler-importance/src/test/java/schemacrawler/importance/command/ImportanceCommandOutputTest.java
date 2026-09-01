/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static schemacrawler.test.ExecutableTestUtility.executableExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
class ImportanceCommandOutputTest {

  private static SchemaRetrievalOptions schemaRetrievalOptions;

  @BeforeAll
  static void createSchemaRetrievalOptions() throws IOException {
    schemaRetrievalOptions = DatabaseTestUtility.newSchemaRetrievalOptions();
  }

  @ParameterizedTest
  @EnumSource(ImportanceReportOutputFormat.class)
  void producesFilteredImportanceReport(
      final ImportanceReportOutputFormat outputFormat,
      final DatabaseConnectionSource connectionSource)
      throws Exception {
    final LimitOptions limitOptions =
        LimitOptionsBuilder.builder().includeSchemas(new IncludeAll()).toOptions();
    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("importance");
    executable.setSchemaCrawlerOptions(
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(
                LoadOptionsBuilder.builder().withInfoLevel(InfoLevel.maximum).toOptions())
            .withLimitOptions(limitOptions));
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
    final Config config = ConfigUtility.newConfig();
    config.put("table-filter", ".*\\.BOOKS$");
    executable.setAdditionalConfiguration(config);

    final String output =
        Files.readString(
            Path.of(
                outputOf(executableExecution(connectionSource, executable, outputFormat))
                    .getResourceString()));

    assertThat(output, containsString("BOOKS"));
    assertFalse(output.contains("PUBLIC.BOOKS.AUTHORS"));
    switch (outputFormat) {
      case json -> {
        assertThat(output, containsString("\"tableFullName\""));
        assertThat(output, containsString("\"nodeId\""));
        assertThat(output, containsString("\"graphMetrics\""));
        assertThat(output, containsString("\"tableCounts\""));
        assertThat(output, containsString("\"tableTraits\""));
        assertThat(output, containsString("\"dependencyReachabilityCount\""));
        assertThat(output, containsString("\"impactReachabilityCount\""));
      }
      case yaml -> {
        assertThat(output, containsString("tableFullName:"));
        assertThat(output, containsString("nodeId:"));
        assertThat(output, containsString("graphMetrics:"));
        assertThat(output, containsString("dependencyReachabilityCount:"));
        assertThat(output, containsString("impactReachabilityCount:"));
      }
      case text -> {
        assertThat(output, containsString("graph metrics:"));
        assertThat(output, containsString("dependency-reachability-count="));
        assertThat(output, containsString("impact-reachability-count="));
        assertThat(output, containsString("table traits:"));
        assertThat(output, not(containsString("nodeId")));
      }
    }
  }
}
