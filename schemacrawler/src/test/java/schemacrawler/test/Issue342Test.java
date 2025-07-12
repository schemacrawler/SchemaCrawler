/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static java.io.File.createTempFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.test.utility.ExecutableTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
public class Issue342Test {

  @Test
  public void unsupportedOutputFormat(final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule("PUBLIC.BOOKS"));
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder()
            // Set what details are required in the schema - this affects the
            // time taken to crawl the schema
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final Path outputFile = createTempFile("schemacrawler", ".dat").toPath();
    final OutputOptions outputOptions = ExecutableTestUtility.newOutputOptions("json", outputFile);
    final String command = "schema";

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setOutputOptions(outputOptions);
    executable.setDataSource(dataSource);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    final Exception exception =
        assertThrows(ExecutionRuntimeException.class, () -> executable.execute());
    assertThat(
        exception.getMessage(), is("Output format <json> not supported for command <schema>"));
  }
}
