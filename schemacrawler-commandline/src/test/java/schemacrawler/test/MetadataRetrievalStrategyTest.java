/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.utility.SystemExitException;

@WithTestDatabase
@ResolveTestContext
@CaptureSystemStreams
public class MetadataRetrievalStrategyTest {

  private static final String METADATA_RETRIEVAL_STRATEGY_OUTPUT =
      "metadata_retrieval_strategy_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(METADATA_RETRIEVAL_STRATEGY_OUTPUT);
  }

  @Test
  public void overrideMetadataRetrievalStrategyDataDictionary(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.minimum;
    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, Object> config = new HashMap<>();
    config.put(
        "schemacrawler.schema.retrieval.strategy.tables",
        MetadataRetrievalStrategy.data_dictionary_all.name());

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", infoLevel.name());

    // Check that System.err has an error, since the SQL for retrieving tables was not provided
    restoreSystemProperties(
        () -> {
          System.setProperty("SC_EXIT_WITH_EXCEPTION", "true");
          assertThrows(
              SystemExitException.class,
              () ->
                  assertThat(
                      outputOf(
                          commandlineExecution(
                              connectionInfo,
                              schemaTextDetailType.name(),
                              argsMap,
                              config,
                              outputFormat)),
                      hasNoContent()));
        });

    assertThat(
        outputOf(streams.err()),
        hasSameContentAs(
            classpathResource(
                METADATA_RETRIEVAL_STRATEGY_OUTPUT
                    + testContext.testMethodName()
                    + ".stderr.txt")));
  }

  @Test
  public void overrideMetadataRetrievalStrategyNone(
      final TestContext testContext,
      final DatabaseConnectionInfo connectionInfo,
      final CapturedSystemStreams streams)
      throws Exception {

    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.schema;
    final InfoLevel infoLevel = InfoLevel.minimum;
    final OutputFormat outputFormat = TextOutputFormat.text;

    final Map<String, Object> config = new HashMap<>();
    config.put(
        "schemacrawler.schema.retrieval.strategy.tables", MetadataRetrievalStrategy.none.name());

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", infoLevel.name());

    // Check that System.err has an error
    assertThat(
        outputOf(
            commandlineExecution(
                connectionInfo, schemaTextDetailType.name(), argsMap, config, outputFormat)),
        hasSameContentAs(
            classpathResource(
                METADATA_RETRIEVAL_STRATEGY_OUTPUT
                    + testContext.testMethodName()
                    + ".stdout.txt")));

    assertThat(outputOf(streams.err()), hasNoContent());
  }
}
