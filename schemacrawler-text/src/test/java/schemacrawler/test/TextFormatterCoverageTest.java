/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasNoContent;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;
import static us.fatehi.utility.Utility.isBlank;

import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.PartialDatabaseObject;
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
import schemacrawler.tools.state.AbstractExecutionState;
import schemacrawler.tools.text.formatter.base.BaseFormatter;
import schemacrawler.tools.text.formatter.schema.SchemaTextFormatter;
import schemacrawler.tools.traversal.ModelHelper;
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

  @Test
  public void tableVisibilityUsesSelectedKeysAndPartialStatus() {
    final LightTable selectedTable = new LightTable(new SchemaReference(), "SELECTED_TABLE");
    final LightTable excludedTable = new LightTable(new SchemaReference(), "EXCLUDED_TABLE");
    final PartialTable partialTable = new PartialTable(new SchemaReference(), "PARTIAL_TABLE");
    selectedTable.setAttribute("schemacrawler.filtered_out", true);
    final Set<?> selectedTableKeys = Set.of(selectedTable.key(), partialTable.key());

    try (final TestWriter out = new TestWriter()) {
      final OutputOptions outputOptions =
          OutputOptionsBuilder.builder()
              .withOutputFormatValue(TextOutputFormat.text.name())
              .withOutputWriter(out)
              .toOptions();
      final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.builder().toOptions();
      final TestFormatter formatter =
          new TestFormatter(
              textOptions, outputOptions, table -> selectedTableKeys.contains(table.key()));

      assertThat(formatter.isFiltered(selectedTable), is(false));
      assertThat(formatter.isFiltered(excludedTable), is(true));
      assertThat(formatter.isFiltered(partialTable), is(true));
      assertThat(formatter.isFiltered(null), is(true));
      formatter.end();
    }
  }

  @Test
  public void tableUsedByObjectsRespectSelectedTableVisibility() throws Exception {
    final SchemaReference schema = new SchemaReference();
    final LightTable excludedTable = new LightTable(schema, "EXCLUDED_TABLE");
    final LightTable table =
        new TableWithUsedByObjects(schema, "SELECTED_TABLE", List.of(excludedTable));
    final Set<?> selectedTableKeys = Set.of(table.key());
    final TestWriter testout = new TestWriter();

    try (final TestWriter out = testout) {
      final OutputOptions outputOptions =
          OutputOptionsBuilder.builder()
              .withOutputFormatValue(TextOutputFormat.text.name())
              .withOutputWriter(out)
              .toOptions();
      final SchemaTextFormatter formatter =
          new SchemaTextFormatter(
              SchemaTextDetailType.details,
              SchemaTextOptionsBuilder.builder().toOptions(),
              outputOptions,
              Identifiers.STANDARD,
              ModelHelper.from(new AbstractExecutionState() {}),
              visibleTable -> selectedTableKeys.contains(visibleTable.key()));

      formatter.handle(table);
      formatter.end();
    }

    final String output = Files.readString(testout.getFilePath());
    assertThat(output, not(containsString("Used By Objects")));
    assertThat(output, not(containsString(excludedTable.getFullName())));
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
      final ModelHelper modelHelper = ModelHelper.from(new AbstractExecutionState() {});
      final SchemaTextFormatter formatter =
          new SchemaTextFormatter(
              SchemaTextDetailType.details,
              textOptions,
              outputOptions,
              Identifiers.STANDARD,
              modelHelper);

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

  private static final class PartialTable extends LightTable implements PartialDatabaseObject {

    private PartialTable(final SchemaReference schema, final String name) {
      super(schema, name);
    }
  }

  private static final class TableWithUsedByObjects extends LightTable {

    private final Collection<DatabaseObject> usedByObjects;

    private TableWithUsedByObjects(
        final SchemaReference schema,
        final String name,
        final Collection<DatabaseObject> usedByObjects) {
      super(schema, name);
      this.usedByObjects = List.copyOf(usedByObjects);
    }

    @Override
    public Collection<DatabaseObject> getUsedByObjects() {
      return usedByObjects;
    }
  }

  private static final class TestFormatter extends BaseFormatter<SchemaTextOptions> {

    private TestFormatter(
        final SchemaTextOptions options,
        final OutputOptions outputOptions,
        final Predicate<Table> tableVisibilityPredicate) {
      super(
          SchemaTextDetailType.details,
          options,
          outputOptions,
          Identifiers.STANDARD,
          tableVisibilityPredicate);
    }

    @Override
    public void begin() {}

    @Override
    public void handleHeader(final CrawlInfo crawlInfo) {}

    @Override
    public void handleHeaderEnd() {}

    @Override
    public void handleHeaderStart() {}

    private boolean isFiltered(final Table table) {
      return isTableFiltered(table);
    }
  }
}
