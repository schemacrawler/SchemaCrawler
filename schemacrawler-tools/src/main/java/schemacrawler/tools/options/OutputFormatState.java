/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.options;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.ArrayList;
import java.util.List;

public final class OutputFormatState implements OutputFormat {

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
    return String.format("%s %s", formatSpecifiers, description);
  }
}
