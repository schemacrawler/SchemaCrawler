/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.scripting.options;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;
import us.fatehi.utility.IOUtility;

public abstract class LanguageOptionsBuilder<
        L extends LanguageType<?>, O extends LanguageOptions<L>>
    implements OptionsBuilder<LanguageOptionsBuilder<L, O>, O>,
        ConfigOptionsBuilder<LanguageOptionsBuilder<L, O>, O> {

  private final L defaultLanguage;
  private final String languageKey;
  private final String resourceKey;
  private L language;
  private String script;

  protected LanguageOptionsBuilder(
      final String languageKey, final String resourceKey, final L defaultLanguage) {
    this.languageKey = requireNonNull(languageKey, "No language key provided");
    this.resourceKey = requireNonNull(resourceKey, "No resource key provided");
    this.defaultLanguage = requireNonNull(defaultLanguage, "No default language provided");
  }

  @Override
  public LanguageOptionsBuilder<L, O> fromConfig(final Config config) {
    script = getScript(config);
    // Language may be inferred from script extension, so set it afterwards
    final String languageString = getLanguageFromConfig(config);
    language = languageFromString(languageString);
    return this;
  }

  @Override
  public LanguageOptionsBuilder<L, O> fromOptions(final O options) {
    if (options != null) {
      language = options.getLanguage();
      script = options.getScript();
    }
    return this;
  }

  public L getLanguage() {
    return language;
  }

  public String getScript() {
    return script;
  }

  public void setLanguage(final L language) {
    this.language = requireNonNull(language, "No language type provided");
  }

  public void setScript(final String script) {
    this.script = script;
  }

  @Override
  public Config toConfig() {
    throw new UnsupportedOperationException();
  }

  protected abstract L languageFromString(String languageName);

  private String getLanguageFromConfig(final Config config) {
    // Check if language is specified
    final String language = config.getStringValue(languageKey, null);
    if (!isBlank(language)) {
      return language;
    }

    // Use the script file extension if the language is not specified
    final String fileExtension = IOUtility.getFileExtension(script);
    if (!isBlank(fileExtension)) {
      return fileExtension;
    }

    return defaultLanguage.toString();
  }

  private String getScript(final Config config) {
    return config.getStringValue(resourceKey, null);
  }
}
