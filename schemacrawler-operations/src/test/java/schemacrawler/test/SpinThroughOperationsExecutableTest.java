/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.EnumSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.command.text.operation.options.OperationsOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.TestUtility;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class SpinThroughOperationsExecutableTest {

  private static final String SPIN_THROUGH_OPERATIONS_OUTPUT = "spin_through_operations_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(SPIN_THROUGH_OPERATIONS_OUTPUT);
  }

  protected static Stream<Arguments> spinThroughArguments() {
    return EnumSet.complementOf(EnumSet.of(OperationType.tablesample)).stream()
        .flatMap(
            operation ->
                EnumSet.complementOf(EnumSet.of(InfoLevel.unknown)).stream()
                    .flatMap(
                        infoLevel ->
                            EnumSet.allOf(OperationsOutputFormat.class).stream()
                                .map(
                                    outputFormat ->
                                        Arguments.of(operation, infoLevel, outputFormat))));
  }

  private static String referenceFile(
      final OperationType operation, final InfoLevel infoLevel, final OutputFormat outputFormat) {
    final String referenceFile =
        "%d%d.%s_%s.%s"
            .formatted(
                operation.ordinal(),
                infoLevel.ordinal(),
                operation,
                infoLevel,
                outputFormat.getFormat());
    return referenceFile;
  }

  @DisplayName("Spin through operations for output")
  @ParameterizedTest()
  @MethodSource("spinThroughArguments")
  public void spinThroughOperationsExecutable(
      final OperationType operation,
      final InfoLevel infoLevel,
      final OutputFormat outputFormat,
      final DatabaseConnectionSource dataSource)
      throws Exception {

    // Special case where no output is generated
    if (infoLevel == InfoLevel.minimum && operation == OperationType.dump) {
      return;
    }

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeAllSequences()
            .includeAllSynonyms()
            .includeAllRoutines();
    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(infoLevel.toSchemaInfoLevel());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    schemaTextOptionsBuilder.noInfo(false);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(operation.name());
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    final String referenceFile =
        SPIN_THROUGH_OPERATIONS_OUTPUT + referenceFile(operation, infoLevel, outputFormat);
    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(referenceFile), outputFormat));
  }
}
