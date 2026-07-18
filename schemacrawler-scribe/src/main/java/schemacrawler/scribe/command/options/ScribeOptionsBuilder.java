/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Locale;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.utility.OptionsBuilder;

/** Builder for immutable {@link ScribeOptions}. */
public final class ScribeOptionsBuilder
    implements OptionsBuilder<ScribeOptionsBuilder, ScribeOptions>,
        ConfigOptionsBuilder<ScribeOptionsBuilder, ScribeOptions> {

  private static final String SCHEMACRAWLER_SCRIBE = "schemacrawler.scribe.";

  private static final String CLI_EXPANDED_OUTPUT = "expanded-output";
  private static final String CLI_INCLUDE_LINT = "include-lint";
  private static final String CLI_LANGUAGE = "language";

  private static final String EXPANDED_OUTPUT = SCHEMACRAWLER_SCRIBE + CLI_EXPANDED_OUTPUT;
  private static final String INCLUDE_LINT = SCHEMACRAWLER_SCRIBE + CLI_INCLUDE_LINT;
  private static final String LANGUAGE = SCHEMACRAWLER_SCRIBE + CLI_LANGUAGE;

  /**
   * Creates a new builder, with the default option values.
   *
   * @return New builder
   */
  public static ScribeOptionsBuilder builder() {
    return new ScribeOptionsBuilder();
  }

  /**
   * Honors a bare command-line option name over its prefixed {@code schemacrawler.scribe.}
   * config-file equivalent, when both could be present in a merged {@link Config}.
   */
  private static String resolveKey(
      final Config config, final String cliKey, final String prefixedKey) {
    return config.containsKey(cliKey) ? cliKey : prefixedKey;
  }

  private boolean expandedOutput;
  private boolean includeLint;
  private Locale locale;

  private String title;

  private ScribeOptionsBuilder() {
    // Use the default field values
    includeLint = false;
    locale = Locale.getDefault();
    title = "";
    expandedOutput = false;
  }

  /** {@inheritDoc} */
  @Override
  public ScribeOptionsBuilder fromConfig(final Config config) {
    if (config == null) {
      return this;
    }
    includeLint = config.getBooleanValue(resolveKey(config, CLI_INCLUDE_LINT, INCLUDE_LINT), false);
    final String languageTag =
        config.getStringValue(resolveKey(config, CLI_LANGUAGE, LANGUAGE), "");
    locale = isBlank(languageTag) ? Locale.getDefault() : Locale.forLanguageTag(languageTag);
    expandedOutput =
        config.getBooleanValue(resolveKey(config, CLI_EXPANDED_OUTPUT, EXPANDED_OUTPUT), false);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ScribeOptionsBuilder fromOptions(final ScribeOptions options) {
    if (options == null) {
      return this;
    }
    title = options.getTitle();
    includeLint = options.isIncludeLint();
    locale = options.getLocale();
    expandedOutput = options.isExpandedOutput();
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Config toConfig() {
    final Config config = ConfigUtility.newConfig();
    config.put(INCLUDE_LINT, includeLint);
    config.put(LANGUAGE, locale.toLanguageTag());
    config.put(EXPANDED_OUTPUT, expandedOutput);
    return config;
  }

  /** {@inheritDoc} */
  @Override
  public ScribeOptions toOptions() {
    return new ScribeOptions(title, includeLint, locale, expandedOutput);
  }

  /**
   * Sets whether to write the report as expanded files and folders instead of a single ZIP archive.
   *
   * @param value Whether to write expanded output
   * @return Builder
   */
  public ScribeOptionsBuilder withExpandedOutput(final boolean value) {
    expandedOutput = value;
    return this;
  }

  /**
   * Sets whether to include lint results in the report.
   *
   * @param value Whether to include lint results
   * @return Builder
   */
  public ScribeOptionsBuilder withIncludeLint(final boolean value) {
    includeLint = value;
    return this;
  }

  /**
   * Sets the locale to use for localized report text.
   *
   * @param value Locale for the report
   * @return Builder
   */
  public ScribeOptionsBuilder withLocale(final Locale value) {
    locale = value == null ? Locale.getDefault() : value;
    return this;
  }

  /**
   * Sets the report title.
   *
   * @param value Report title
   * @return Builder
   */
  public ScribeOptionsBuilder withTitle(final String value) {
    title = trimToEmpty(value);
    return this;
  }
}
