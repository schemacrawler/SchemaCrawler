/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.options;

import java.util.List;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.UtilityMarker;

/** Output formats supported by the importance report. */
@UtilityMarker
public enum ImportanceReportOutputFormat implements OutputFormat {
  text("Plain text format", "txt"),
  json("JavaScript Object Notation (JSON) format"),
  yaml("YAML Ain't Markup Language (YAML) format");

  public static ImportanceReportOutputFormat fromFormat(final String format) {
    for (final ImportanceReportOutputFormat outputFormat : values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return outputFormat;
      }
    }
    throw new IllegalArgumentException("Unsupported importance report format: " + format);
  }

  public static boolean isSupportedFormat(final String format) {
    for (final ImportanceReportOutputFormat outputFormat : values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return true;
      }
    }
    return false;
  }

  private final OutputFormatState outputFormatState;

  ImportanceReportOutputFormat(final String description) {
    outputFormatState = new OutputFormatState(name(), description);
  }

  ImportanceReportOutputFormat(final String description, final String formatSpecifier) {
    outputFormatState = new OutputFormatState(formatSpecifier, description, name());
  }

  @Override
  public String getDescription() {
    return outputFormatState.getDescription();
  }

  @Override
  public String getFormat() {
    return outputFormatState.getFormat();
  }

  @Override
  public List<String> getFormats() {
    return outputFormatState.getFormats();
  }

  @Override
  public String toString() {
    return outputFormatState.toString();
  }
}
