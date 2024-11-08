/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.clean;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import schemacrawler.test.utility.TestUtility;
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

  private SchemaRetrievalOptions schemaRetrievalOptions;

  @Test
  public void compareListOutput(final DatabaseConnectionSource dataSource) throws Exception {
    clean(LIST_OUTPUT);

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSchemas(new RegularExpressionInclusionRule(".*\\.BOOKS"))
            .includeSequences(new IncludeAll())
            .includeSynonyms(new IncludeAll())
            .includeRoutines(new IncludeAll());
    final LimitOptions limitOptions = limitOptionsBuilder.toOptions();

    assertAll(
        outputFormats()
            .map(
                outputFormat ->
                    () -> {
                      compareListOutput(
                          dataSource,
                          limitOptions,
                          tablesOutputTextOptions(),
                          "list",
                          outputFormat,
                          "list_tables");
                    }));

    assertAll(
        outputFormats()
            .map(
                outputFormat ->
                    () -> {
                      compareListOutput(
                          dataSource,
                          limitOptions,
                          allOutputTextOptions(),
                          "list",
                          outputFormat,
                          "list_all");
                    }));
  }

  @BeforeAll
  public void schemaRetrievalOptions() throws IOException {
    schemaRetrievalOptions = TestUtility.newSchemaRetrievalOptions();
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

    final String referenceFile = String.format("%s.%s", title, outputFormat.getFormat());

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
