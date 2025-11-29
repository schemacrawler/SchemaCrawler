/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.string.StringFormat;

/** Enumeration for operations format type. */
public enum OperationsOutputFormat implements OutputFormat {
  text("Plain text format", "txt"),
  html("HyperText Markup Language (HTML) format"),
  json("JavaScript Object Notation (JSON) serialization format");

  private static final Logger LOGGER = Logger.getLogger(OperationsOutputFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format Operations output format.
   * @return OperationsOutputFormat
   */
  public static OperationsOutputFormat fromFormat(final String format) {
    final OperationsOutputFormat outputFormat = fromFormatOrNull(format);
    if (outputFormat == null) {
      LOGGER.log(Level.CONFIG, new StringFormat("Unknown format <%s>, using default", format));
      return json;
    }
    return outputFormat;
  }

  /**
   * Checks if the value of the format is supported.
   *
   * @return True if the format is a text output format
   */
  public static boolean isSupportedFormat(final String format) {
    return fromFormatOrNull(format) != null;
  }

  private static OperationsOutputFormat fromFormatOrNull(final String format) {
    if (isBlank(format)) {
      return null;
    }
    for (final OperationsOutputFormat outputFormat : OperationsOutputFormat.values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return outputFormat;
      }
    }
    return null;
  }

  private final OutputFormatState outputFormatState;

  OperationsOutputFormat(final String description) {
    outputFormatState = new OutputFormatState(name(), description);
  }

  OperationsOutputFormat(final String description, final String formatSpecifier) {
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
