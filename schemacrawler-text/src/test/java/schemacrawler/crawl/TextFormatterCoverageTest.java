/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.schema.SchemaTextFormatter;
import us.fatehi.test.utility.TestObjectUtility;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.database.ConnectionInfoBuilder;

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
    columnDataType.setEnumValues(List.of("VALUE1", "VALUE2"));
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
    final MutableDatabaseInfo dbInfo = makeDatabaseInfo();

    checkTextOutput(
        formatter -> {
          formatter.handleHeader((CrawlInfo) null);
          formatter.handleInfo(dbInfo);
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

    final MutableDatabaseInfo dbInfo = makeDatabaseInfo();

    checkTextOutput(formatter -> formatter.handleInfo(dbInfo), testContext.testMethodFullName());
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

  private MutableDatabaseInfo makeDatabaseInfo() throws SQLException {
    final Connection mockConnection = TestObjectUtility.mockConnection();
    final ConnectionInfoBuilder connectionInfoBuilder =
        ConnectionInfoBuilder.builder(mockConnection);
    final MutableDatabaseInfo dbInfo =
        new MutableDatabaseInfo(connectionInfoBuilder.buildDatabaseInformation());
    return dbInfo;
  }
}
