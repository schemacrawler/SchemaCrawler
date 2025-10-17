/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;

import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptions;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
@TestInstance(PER_CLASS)
public class ListCommandOutputTest {

  private static final String LIST_OUTPUT = "list_output/";

  @BeforeAll
  public static void cleanListOutput() throws Exception {
    clean(LIST_OUTPUT);
  }

  private SchemaRetrievalOptions schemaRetrievalOptions;

  @DisplayName("Compare list output")
  @ParameterizedTest(name = "with output format {0}")
  @EnumSource(
      value = TextOutputFormat.class,
      names = {"text", "html"})
  public void compareListOutput(
      final OutputFormat outputFormat, final DatabaseConnectionSource dataSource) throws Exception {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
            .includeSequences(new IncludeAll())
            .includeSynonyms(new IncludeAll())
            .includeRoutines(new IncludeAll());
    final LimitOptions limitOptions = limitOptionsBuilder.toOptions();

    compareListOutput(
        dataSource, limitOptions, tablesOutputTextOptions(), "list", outputFormat, "list_tables");

    compareListOutput(
        dataSource, limitOptions, allOutputTextOptions(), "list", outputFormat, "list_all");
  }

  @BeforeAll
  public void schemaRetrievalOptions() throws IOException {
    schemaRetrievalOptions = DatabaseTestUtility.newSchemaRetrievalOptions();
  }

  protected Stream<OutputFormat> outputFormats() {
    return Stream.of(TextOutputFormat.text, TextOutputFormat.html);
  }

  private SchemaTextOptions allOutputTextOptions() {
    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder
        .noRemarks()
        .noSchemaCrawlerInfo()
        .showDatabaseInfo(false)
        .showJdbcDriverInfo(false);
    return textOptionsBuilder.toOptions();
  }

  private void compareListOutput(
      final DatabaseConnectionSource dataSource,
      final LimitOptions limitOptions,
      final SchemaTextOptions textOptions,
      final String command,
      final OutputFormat outputFormat,
      final String title)
      throws Exception {

    final String referenceFile = "%s.%s".formatted(title, outputFormat.getFormat());

    final LoadOptions loadOptions =
        LoadOptionsBuilder.builder().withInfoLevel(InfoLevel.maximum).toOptions();

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptions)
            .withLimitOptions(limitOptions);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);

    assertThat(
        outputOf(executableExecution(dataSource, executable, outputFormat)),
        hasSameContentAndTypeAs(classpathResource(LIST_OUTPUT + referenceFile), outputFormat));
  }

  private SchemaTextOptions tablesOutputTextOptions() {
    final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder.builder();
    textOptionsBuilder
        .noRemarks()
        .noSchemaCrawlerInfo()
        .showDatabaseInfo(false)
        .showJdbcDriverInfo(false)
        .noSchemas()
        .noSynonyms()
        .noSequences()
        .noRoutines();
    return textOptionsBuilder.toOptions();
  }
}
