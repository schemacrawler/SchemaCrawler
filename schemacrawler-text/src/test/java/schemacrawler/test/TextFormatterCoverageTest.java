/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.Utility.isBlank;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightDatabaseInfo;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.schema.SchemaTextFormatter;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class TextFormatterCoverageTest {

  private static final String FORMATTER_COVERAGE_OUTPUT = "formatter_coverage/";

  @Test
  public void blankTable(final TestContext testContext) throws Exception {

    final Table table = new LightTable(new SchemaReference(), "TEST_TABLE");

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void enumValuesColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addEnumeratedColumn("ENUM_VALUES_COLUMN");

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void generatedColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addGeneratedColumn("GENERATED_COLUMN");

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void hiddenColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addHiddenColumn("HIDDEN_COLUMN");

    checkTextOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void nullCrawlInfo(final TestContext testContext) throws Exception {
    final DatabaseInfo dbInfo = new LightDatabaseInfo();

    checkTextOutput(
        formatter -> {
          formatter.handleHeader((CrawlInfo) null);
          formatter.handleInfo(dbInfo);
        },
        testContext.testMethodFullName());
  }

  @Test
  public void nullTable(final TestContext testContext) throws Exception {
    final Table table = null;
    checkTextOutputForTable(table, null);
  }

  @Test
  public void serverInfo(final TestContext testContext) throws Exception {

    final DatabaseInfo dbInfo = new LightDatabaseInfo();

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
}
