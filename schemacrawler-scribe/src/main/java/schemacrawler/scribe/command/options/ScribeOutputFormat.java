/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import schemacrawler.scribe.okf.OkfScribeRenderer;
import schemacrawler.scribe.renderer.ScribeRenderer;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;

/** Supported Scribe output formats and their renderer constructors. */
public enum ScribeOutputFormat implements OutputFormat {
  okf("Google Open Knowledge Format (OKF) bundle");

  public static ScribeOutputFormat fromFormat(final String format) {
    ScribeOutputFormat scribeFormat = fromFormatOrNull(format);
    if (scribeFormat != null) {
      return scribeFormat;
    }
    return okf;
  }

  public static ScribeOutputFormat fromFormatOrNull(final String format) {
    // Handle defaults
    if (isBlank(format) || "text".equals(format)) {
      return okf;
    }
    for (final ScribeOutputFormat outputFormat : values()) {
      if (outputFormat.name().equalsIgnoreCase(format)) {
        return outputFormat;
      }
    }
    // Return null if a bad, non-empty format is returned
    return null;
  }

  public static boolean isSupportedFormat(final String format) {
    return fromFormatOrNull(format) != null;
  }

  public static List<String> supportedFormats() {
    return List.of(okf.name());
  }

  private final OutputFormatState outputFormatState;

  ScribeOutputFormat(final String description) {
    outputFormatState = new OutputFormatState(name(), description);
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

  public ScribeRenderer newRenderer() {
    return switch (this) {
      case okf -> new OkfScribeRenderer();
    };
  }

  @Override
  public String toString() {
    return outputFormatState.toString();
  }
}
