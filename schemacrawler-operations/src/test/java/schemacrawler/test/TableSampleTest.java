/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.operation.options.OperationsOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class TableSampleTest {

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  public void tableSampleJsonOutput(final DatabaseConnectionSource dataSource) throws Exception {

    final OperationType operation = OperationType.tablesample;
    final OutputFormat outputFormat = OperationsOutputFormat.json;

    final String command = operation.name();
    final Config config = new Config();
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder commonTextOptions = SchemaTextOptionsBuilder.builder();
    commonTextOptions.fromConfig(config);
    commonTextOptions.noInfo();
    commonTextOptions.sortTables(true);
    config.merge(commonTextOptions.toConfig());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(config);

    final Path outputFile = executableExecution(dataSource, executable, outputFormat);

    final Catalog catalog = executable.getCatalog();
    final int numTables = catalog.getTables().size();
    assertThat(numTables, is(14));

    // Validate JSON file
    final ObjectMapper mapper = new ObjectMapper();
    JsonNode root;
    try {
      root = mapper.readTree(Files.newBufferedReader(outputFile));
    } catch (final Exception e) {
      fail("Invalid JSON in 'tablesample': " + e.getMessage());
      return;
    }
    // Confirm only 10 rows are returned for each table
    for (int i = 0; i < numTables; i++) {
      final JsonNode dataRowsArray = root.at("/" + i + "/data");
      assertThat(dataRowsArray.isArray(), is(true));
      assertThat(dataRowsArray.size(), is(lessThanOrEqualTo(10)));
    }
  }
}
