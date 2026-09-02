/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.report;

import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import schemacrawler.importance.cache.TableImportanceMetrics;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

/** Writes importance report entries in the requested format. */
public final class ImportanceReportWriter {

  private static final ObjectMapper JSON_MAPPER =
      tools.jackson.databind.json.JsonMapper.builder()
          .enable(INDENT_OUTPUT)
          .changeDefaultPropertyInclusion(
              inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
          .build();
  private static final ObjectMapper YAML_MAPPER =
      YAMLMapper.builder()
          .enable(INDENT_OUTPUT)
          .changeDefaultPropertyInclusion(
              inclusion -> inclusion.withValueInclusion(JsonInclude.Include.NON_EMPTY))
          .build();

  public static void write(
      final List<ImportanceReportEntry> entries,
      final ImportanceReportOutputFormat outputFormat,
      final OutputOptions outputOptions)
      throws IOException {
    try (final Writer writer = outputOptions.openNewOutputWriter()) {
      switch (outputFormat) {
        case json -> JSON_MAPPER.writeValue(writer, entries);
        case yaml -> YAML_MAPPER.writeValue(writer, entries);
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
