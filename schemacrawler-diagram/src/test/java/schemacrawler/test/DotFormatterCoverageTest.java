/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOptionsBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.diagram.SchemaDotFormatter;
import us.fatehi.test.utility.TestWriter;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class DotFormatterCoverageTest {

  private static final String FORMATTER_COVERAGE_OUTPUT = "formatter_coverage/";

  @Test
  public void blankTable(final TestContext testContext) throws Exception {

    final Table table = new LightTable(new SchemaReference(), "TEST_TABLE");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void generatedColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addGeneratedColumn("GENERATED_COLUMN");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void hiddenColumnTable(final TestContext testContext) throws Exception {

    final LightTable table = new LightTable(new SchemaReference(), "TEST_TABLE");
    table.addHiddenColumn("HIDDEN_COLUMN");

    checkDotOutputForTable(table, testContext.testMethodFullName());
  }

  @Test
  public void nullTable(final TestContext testContext) throws Exception {

    final Table table = null;
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
