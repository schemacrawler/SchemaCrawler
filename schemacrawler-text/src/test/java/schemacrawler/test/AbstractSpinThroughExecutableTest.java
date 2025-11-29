/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.util.EnumSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
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
public abstract class AbstractSpinThroughExecutableTest {

  private static final String SPIN_THROUGH_OUTPUT = "spin_through_output/";

  @BeforeAll
  public static void clean() throws Exception {
    TestUtility.clean(SPIN_THROUGH_OUTPUT);
  }

  private static Stream<InfoLevel> infoLevels() {
    return EnumSet.complementOf(EnumSet.of(InfoLevel.unknown)).stream();
  }

  private static String referenceFile(
      final SchemaTextDetailType schemaTextDetailType,
      final InfoLevel infoLevel,
      final OutputFormat outputFormat) {
    final String referenceFile =
        "%d%d.%s_%s.%s"
            .formatted(
                schemaTextDetailType.ordinal(),
                infoLevel.ordinal(),
                schemaTextDetailType,
                infoLevel,
                outputFormat.getFormat());
    return referenceFile;
  }

  @ParameterizedTest
  @EnumSource(SchemaTextDetailType.class)
  public void spinThroughExecutable(
      final SchemaTextDetailType schemaTextDetailType, final DatabaseConnectionSource dataSource)
      throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions =
        DatabaseTestUtility.newSchemaRetrievalOptions();

    assertAll(
        infoLevels()
            .flatMap(
                infoLevel ->
                    outputFormats()
                        .map(
                            outputFormat ->
                                () -> {
                                  spinThroughExecutable(
                                      dataSource,
                                      schemaRetrievalOptions,
                                      infoLevel,
                                      outputFormat,
                                      schemaTextDetailType);
                                })));
  }

  protected abstract Stream<? extends OutputFormat> outputFormats();

  private void spinThroughExecutable(
      final DatabaseConnectionSource dataSource,
      final SchemaRetrievalOptions schemaRetrievalOptions,
      final InfoLevel infoLevel,
      final OutputFormat outputFormat,
      final SchemaTextDetailType schemaTextDetailType)
      throws Exception {
    final String referenceFile = referenceFile(schemaTextDetailType, infoLevel, outputFormat);

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

    final SchemaCrawlerExecutable executable =
        new SchemaCrawlerExecutable(schemaTextDetailType.name());
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());

    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);

    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAndTypeAs(
            classpathResource(SPIN_THROUGH_OUTPUT + referenceFile), outputFormat));
  }
}
