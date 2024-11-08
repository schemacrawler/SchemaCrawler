/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.lint.options;

import static us.fatehi.utility.Utility.isBlank;

import java.util.List;
import java.util.logging.Level;

import java.util.logging.Logger;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputFormatState;
import us.fatehi.utility.string.StringFormat;

/** Enumeration for text format type. */
public enum LintReportOutputFormat implements OutputFormat {
  text("Plain text format", "txt"),
  html("HyperText Markup Language (HTML) format"),
  json("JavaScript Object Notation (JSON) format"),
  yaml("YAML Ain't Markup Language (YAML) format");

  private static final Logger LOGGER = Logger.getLogger(LintReportOutputFormat.class.getName());

  /**
   * Gets the value from the format.
   *
   * @param format Text output format.
   * @return TextOutputFormat
   */
  public static LintReportOutputFormat fromFormat(final String format) {
    final LintReportOutputFormat outputFormat = fromFormatOrNull(format);
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

  private static LintReportOutputFormat fromFormatOrNull(final String format) {
    if (isBlank(format)) {
      return null;
    }
    for (final LintReportOutputFormat outputFormat : LintReportOutputFormat.values()) {
      if (outputFormat.outputFormatState.isSupportedFormat(format)) {
        return outputFormat;
      }
    }
    return null;
  }

  private final OutputFormatState outputFormatState;

  LintReportOutputFormat(final String description) {
    outputFormatState = new OutputFormatState(name(), description);
  }

  LintReportOutputFormat(final String description, final String formatSpecifier) {
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
