/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.report;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import schemacrawler.importance.cache.TableImportanceMetrics;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import tools.jackson.databind.ObjectMapper;

/** Writes importance report entries in the requested format. */
public final class ImportanceReportWriter {

  private static final ObjectMapper jsonMapper = JsonUtility.newJsonMapperBuilder().build();
  private static final ObjectMapper yamlMapper = JsonUtility.newJsonMapperBuilder().build();

  public static void write(
      final List<ImportanceReportEntry> entries,
      final ImportanceReportOutputFormat outputFormat,
      final OutputOptions outputOptions)
      throws IOException {
    try (final Writer writer = outputOptions.openNewOutputWriter()) {
      switch (outputFormat) {
        case json -> jsonMapper.writeValue(writer, entries);
        case yaml -> yamlMapper.writeValue(writer, entries);
        case text -> writeText(entries, writer);
      }
    }
  }

  private static void writeText(final List<ImportanceReportEntry> entries, final Writer writer)
      throws IOException {
    for (final ImportanceReportEntry entry : entries) {
      final TableImportanceMetrics metrics = entry.graphMetrics();
      writer.write(
          "%s%n".formatted(entry.tableFullName())
              + "  graph metrics: in-degree=%d, out-degree=%d, betweenness-centrality=%s,"
              + " dependency-reachability-count=%d, impact-reachability-count=%d%n"
                  .formatted(
                      metrics.inDegree(),
                      metrics.outDegree(),
                      metrics.betweennessCentrality(),
                      metrics.dependencyReachabilityCount(),
                      metrics.impactReachabilityCount())
              + "  table traits: %s%n".formatted(entry.tableTraits())
              + "  table counts: %s%n%n".formatted(entry.tableCounts()));
    }
  }

  private ImportanceReportWriter() {
    // Prevent instantiation
  }
}
