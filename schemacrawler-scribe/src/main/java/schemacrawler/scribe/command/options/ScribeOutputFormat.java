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

/** Supported Scribe output formats and their renderer constructors. */
public enum ScribeOutputFormat {
  okf;

  public static boolean isSupportedFormat(final String format) {
    return fromFormatOrNull(format) != null;
  }

  public static List<String> supportedFormats() {
    return List.of(okf.name());
  }

  public static ScribeOutputFormat fromFormatOrNull(final String format) {
    if (isBlank(format)) {
      return null;
    }
    for (final ScribeOutputFormat outputFormat : values()) {
      if (outputFormat.name().equalsIgnoreCase(format)) {
        return outputFormat;
      }
    }
    return null;
  }

  public ScribeRenderer newRenderer() {
    return switch (this) {
      case okf -> new OkfScribeRenderer();
    };
  }
}
