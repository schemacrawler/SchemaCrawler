/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.DataTypeType;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.diagram.SchemaDotFormatter;

@ResolveTestContext
public class DotFormatterCoverageTest {

  private static final String FORMATTER_COVERAGE_OUTPUT = "formatter_coverage/";

  @Test
  public void blankTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void generatedColumnTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");
    final MutableColumn column = new MutableColumn(table, "GENERATED_COLUMN");
    column.setColumnDataType(
        new MutableColumnDataType(new SchemaReference(), "DATA_TYPE", DataTypeType.user_defined));
    column.setGenerated(true);
    table.addColumn(column);

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void hiddenColumnTable(final TestContext testContext) throws Exception {

    final MutableTable table = new MutableTable(new SchemaReference(), "TEST_TABLE");
    final MutableColumn column = new MutableColumn(table, "HIDDEN_COLUMN");
    column.setColumnDataType(
        new MutableColumnDataType(new SchemaReference(), "DATA_TYPE", DataTypeType.user_defined));
    column.setHidden(true);
    table.addHiddenColumn(column);

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void nullTable(final TestContext testContext) throws Exception {

    final MutableTable table = null;
    assertThrows(
        NullPointerException.class,
        () -> checkDotOutputForTable(table, testContext.testMethodFullName()));
  }

  private void checkDotOutputForTable(final Table table, final String referenceFileName) {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      final DiagramOptions diagramOptions = DiagramOptionsBuilder.builder().toOptions();
      final OutputOptionsBuilder outputOptionsBuilder =
          OutputOptionsBuilder.builder()
              .withOutputFormatValue(DiagramOutputFormat.scdot.name())
              .withOutputWriter(out);

      final OutputOptions outputOptions = outputOptionsBuilder.toOptions();
      final SchemaDotFormatter formatter =
          new SchemaDotFormatter(
              SchemaTextDetailType.details, diagramOptions, outputOptions, Identifiers.STANDARD);

      formatter.handle(table);
    }
    assertThat(
        outputOf(testout.getFilePath()),
        hasSameContentAs(
            classpathResource(FORMATTER_COVERAGE_OUTPUT + referenceFileName + ".dot")));
  }
}
