/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.command;

import java.io.IOException;
import java.util.List;
import schemacrawler.importance.cache.SchemaGraphCache;
import schemacrawler.importance.options.ImportanceOptions;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.importance.report.ImportanceReportEntry;
import schemacrawler.importance.report.ImportanceReportWriter;
import schemacrawler.importance.service.ReportService;
import schemacrawler.importance.util.SchemaGraphCacheBuilder;
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
    final SchemaGraphCache schemaGraphCache = SchemaGraphCacheBuilder.builder(getCatalog()).build();
    final List<ImportanceReportEntry> entries =
        new ReportService(schemaGraphCache).report(getCommandOptions().getTableInclusionRule());
    try {
      ImportanceReportWriter.write(entries, outputFormat, getOutputOptions());
    } catch (final IOException e) {
      throw new ExecutionRuntimeException("Could not generate importance report", e);
    }
  }

  @Override
  public boolean usesConnection() {
    return false;
  }
}
