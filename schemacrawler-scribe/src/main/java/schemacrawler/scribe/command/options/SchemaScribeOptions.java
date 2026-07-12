/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;
import schemacrawler.tools.command.CommandOptions;

/** Immutable options for the Scribe command. */
public final class SchemaScribeOptions implements CommandOptions {

  private final boolean expandedOutput;
  private final boolean includeLint;
  private final Locale locale;
  private final String title;

  SchemaScribeOptions(
      final String title,
      final boolean includeLint,
      final Locale locale,
      final boolean expandedOutput) {
    this.title = requireNonNull(title, "No title provided");
    this.includeLint = includeLint;
    this.locale = locale == null ? Locale.getDefault() : locale;
    this.expandedOutput = expandedOutput;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SchemaScribeOptions other = (SchemaScribeOptions) obj;
    return includeLint == other.includeLint
        && expandedOutput == other.expandedOutput
        && Objects.equals(locale, other.locale)
        && Objects.equals(title, other.title);
  }

  /**
   * Gets the locale to use for localized report text.
   *
   * @return Locale for the report
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Gets the report title.
   *
   * @return Report title, blank to use the database product name
   */
  public String getTitle() {
    return title;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        Boolean.valueOf(includeLint), Boolean.valueOf(expandedOutput), locale, title);
  }

  /**
   * Whether to write the report as expanded files and folders instead of a single ZIP archive.
   *
   * @return Whether to write expanded output, default {@code false} (ZIP output)
   */
  public boolean isExpandedOutput() {
    return expandedOutput;
  }

  /**
   * Whether to include lint results in the report.
   *
   * @return Whether to include lint results
   */
  public boolean isIncludeLint() {
    return includeLint;
  }
}
