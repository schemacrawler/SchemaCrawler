/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static us.fatehi.utility.Utility.isBlank;
import java.util.Arrays;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.schema.SchemaTextFormatter;

@ResolveTestContext
public class TextFormatterCoverageTest {

  private static final String FORMATTER_COVERAGE_OUTPUT = "formatter_coverage/";

  @Test
  public void blankTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void enumValuesColumnTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");
    final MutableColumnDataType columnDataType =
        new MutableColumnDataType(new SchemaReference(), "DATA_TYPE", DataTypeType.user_defined);
    columnDataType.setEnumValues(Arrays.asList("VALUE1", "VALUE2"));
    final MutableColumn column = new MutableColumn(table, "ENUM_VALUES_COLUMN");
    column.setColumnDataType(columnDataType);
    table.addColumn(column);

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void generatedColumnTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");
    final MutableColumn column = new MutableColumn(table, "GENERATED_COLUMN");
    column.setColumnDataType(
        new MutableColumnDataType(new SchemaReference(), "DATA_TYPE", DataTypeType.user_defined));
    column.setGenerated(true);
    table.addColumn(column);

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void hiddenColumnTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");
    final MutableColumn column = new MutableColumn(table, "HIDDEN_COLUMN");
    column.setColumnDataType(
        new MutableColumnDataType(new SchemaReference(), "DATA_TYPE", DataTypeType.user_defined));
    column.setHidden(true);
    table.addHiddenColumn(column);

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void nullCrawlInfo(final TestContext testContext) throws Exception {
    checkTextOutput(
        formatter -> {
          formatter.handle((CrawlInfo) null);
          formatter.handle(mock(DatabaseInfo.class));
        },
        testContext.testMethodFullName());
  }

  @Test
  public void nullTable(final TestContext testContext) throws Exception {
    final MutableTable table = null;
    checkTextOutputForTable(table, null);
  }

  @Test
  public void serverInfo(final TestContext testContext) throws Exception {

    final MutableDatabaseInfo dbInfo = new MutableDatabaseInfo("FakeDB", "v0.0", "nouser");
    dbInfo.addServerInfo(
        new ImmutableServerInfoProperty("PROP1", "VALUE1", "Server info property"));

    checkTextOutput(formatter -> formatter.handle(dbInfo), testContext.testMethodFullName());
  }

  private void checkTextOutput(
      final Consumer<SchemaTextFormatter> formatterMethod, final String referenceFileName) {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final SchemaTextOptions textOptions =
          SchemaTextOptionsBuilder.builder().showDatabaseInfo().toOptions();
      final OutputOptionsBuilder outputOptionsBuilder =
          OutputOptionsBuilder.builder()
              .withOutputFormatValue(TextOutputFormat.text.name())
              .withOutputWriter(out);

      final OutputOptions outputOptions = outputOptionsBuilder.toOptions();
      final SchemaTextFormatter formatter =
          new SchemaTextFormatter(
              SchemaTextDetailType.details, textOptions, outputOptions, Identifiers.STANDARD);

      formatterMethod.accept(formatter);
    }
    if (isBlank(referenceFileName)) {
      assertThat(outputOf(testout.getFilePath()), hasNoContent());
    } else {
      assertThat(
          outputOf(testout.getFilePath()),
          hasSameContentAs(
              classpathResource(FORMATTER_COVERAGE_OUTPUT + referenceFileName + ".txt")));
    }
  }

  private void checkTextOutputForTable(final Table table, final String referenceFileName) {
    checkTextOutput(formatter -> formatter.handle(table), referenceFileName);
  }
}
