/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.command;

import java.io.IOException;
import schemacrawler.importance.model.SchemaGraphModel;
import schemacrawler.importance.model.builder.SchemaGraphModelBuilder;
import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.importance.report.ImportanceReport;
import schemacrawler.importance.report.ImportanceReportGenerator;
import schemacrawler.importance.report.ImportanceReportWriter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommand;
import us.fatehi.utility.property.PropertyName;

/** Generates a topology-based importance report for database tables and views. */
public final class ImportanceCommand extends AbstractSchemaCrawlerCommand<ImportanceOptions> {

  static final PropertyName COMMAND =
      new PropertyName("importance", "Generate a database object importance report");

  public ImportanceCommand() {
    super(COMMAND);
  }

  @Override
  public void execute() {
    checkCatalog();

    final ImportanceReportOutputFormat outputFormat =
        ImportanceReportOutputFormat.fromFormat(getOutputOptions().getOutputFormatValue());
    final SchemaGraphModel schemaGraphModel = SchemaGraphModelBuilder.builder(getCatalog()).build();
    final InclusionRule tableInclusionRule = getCommandOptions().getTableInclusionRule();
    final int maxTables = getCommandOptions().getMaxTables();
    final ImportanceReport report =
        new ImportanceReportGenerator(schemaGraphModel).report(tableInclusionRule, maxTables);
    try {
      ImportanceReportWriter.write(report, outputFormat, getOutputOptions());
    } catch (final IOException e) {
      throw new ExecutionRuntimeException("Could not generate importance report", e);
    }
  }

  @Override
  public boolean usesConnection() {
    return false;
  }
}
