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
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import tools.jackson.databind.ObjectMapper;

/** Writes importance report entries in the requested format. */
public final class ImportanceReportWriter {

  private static final ObjectMapper jsonMapper = JsonUtility.newJsonMapperBuilder().build();
  private static final ObjectMapper yamlMapper = JsonUtility.newYamlMapperBuilder().build();

  public static void write(
      final ImportanceReport report,
      final ImportanceReportOutputFormat outputFormat,
      final OutputOptions outputOptions)
      throws IOException {
    try (final Writer writer = outputOptions.openNewOutputWriter()) {
      switch (outputFormat) {
        case json -> jsonMapper.writeValue(writer, report);
        case text, yaml -> yamlMapper.writeValue(writer, report);
      }
    }
  }

  private ImportanceReportWriter() {
    // Prevent instantiation
  }
}
