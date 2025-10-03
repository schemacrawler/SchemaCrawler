/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.options;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public final class OutputFormatState implements OutputFormat {

  @Serial private static final long serialVersionUID = -5715099922209080457L;

  private final List<String> formatSpecifiers;
  private final String description;

  public OutputFormatState(
      final String formatSpecifier,
      final String description,
      final String... additionalFormatSpecifiers) {
    requireNotBlank(formatSpecifier, "No format specifier provided");

    this.description = requireNotBlank(description, "No description provided");
    formatSpecifiers = new ArrayList<>();
    formatSpecifiers.add(formatSpecifier);
    if (additionalFormatSpecifiers != null) {
      for (final String additionalFormatSpecifier : additionalFormatSpecifiers) {
        if (!isBlank(additionalFormatSpecifier)) {
          formatSpecifiers.add(additionalFormatSpecifier);
        }
      }
    }
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getFormat() {
    return formatSpecifiers.get(0);
  }

  @Override
  public List<String> getFormats() {
    return new ArrayList<>(formatSpecifiers);
  }

  /**
   * Checks if the provided format is supported.
   *
   * @param format Format to check
   * @return If the format is supported, ignoring case.
   */
  public boolean isSupportedFormat(final String format) {
    for (final String formatSpecifier : formatSpecifiers) {
      if (formatSpecifier.equalsIgnoreCase(format)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "%s %s".formatted(formatSpecifiers, description);
  }
}
