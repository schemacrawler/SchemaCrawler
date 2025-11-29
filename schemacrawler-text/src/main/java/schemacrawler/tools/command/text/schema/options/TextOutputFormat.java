/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema.options;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.string.StringFormat;

/** Enumeration for text format type. */
public enum TextOutputFormat implements OutputFormat {
  text("Plain text format", "txt"),
  html("HyperText Markup Language (HTML) format"),
  tsv("Tab-separated values (TSV) format");

  private static final Logger LOGGER = Logger.getLogger(TextOutputFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format Text output format.
   * @return TextOutputFormat
   */
  public static TextOutputFormat fromFormat(final String format) {
    final TextOutputFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null) {
      LOGGER.log(Level.CONFIG, new StringFormat("Unknown format <%s>, using default", format));
      return text;
    } else {
      return outputFormat;
    }
  }

  /**
   * Checks if the value of the format is supported.
   *
   * @return True if the format is a text output format
   */
  public static boolean isSupportedFormat(final String format) {
    return fromFormatOrNull(format) != null;
  }

  private static TextOutputFormat fromFormatOrNull(final String format) {
    if (isBlank(format)) {
      return null;
    }
    for (final TextOutputFormat outputFormat : TextOutputFormat.values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return outputFormat;
      }
    }
    return null;
  }

  private final OutputFormatState outputFormatState;

  TextOutputFormat(final String description) {
    outputFormatState = new OutputFormatState(name(), description);
  }

  TextOutputFormat(final String description, final String formatSpecifier) {
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
