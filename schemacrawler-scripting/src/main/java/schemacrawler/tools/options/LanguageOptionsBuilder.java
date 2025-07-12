/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.options;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.getFileExtension;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.schemacrawler.OptionsBuilder;

public abstract class LanguageOptionsBuilder<O extends LanguageOptions>
    implements OptionsBuilder<LanguageOptionsBuilder<O>, O>,
        ConfigOptionsBuilder<LanguageOptionsBuilder<O>, O> {

  private final String defaultLanguage;
  private final String languageKey;
  private final String resourceKey;
  private String language;
  private String script;

  protected LanguageOptionsBuilder(
      final String languageKey, final String resourceKey, final String defaultLanguage) {
    this.languageKey = requireNonNull(languageKey, "No language key provided");
    this.resourceKey = requireNonNull(resourceKey, "No resource key provided");
    this.defaultLanguage = requireNonNull(defaultLanguage, "No default language provided");
  }

  @Override
  public LanguageOptionsBuilder<O> fromConfig(final Config config) {
    script = getScript(config);
    // Language may be inferred from script extension, so set it afterwards
    language = getLanguage(config);
    return this;
  }

  @Override
  public LanguageOptionsBuilder<O> fromOptions(final LanguageOptions options) {
    if (options != null) {
      language = options.getLanguage();
      script = options.getScript();
    }
    return this;
  }

  public String getLanguage() {
    return language;
  }

  public String getScript() {
    return script;
  }

  public void setLanguage(final String language) {
    this.language = language;
  }

  public void setScript(final String script) {
    this.script = script;
  }

  @Override
  public Config toConfig() {
    throw new UnsupportedOperationException();
  }

  private final String getLanguage(final Config config) {
    // Check if language is specified
    final String language = config.getStringValue(languageKey, null);
    if (!isBlank(language)) {
      return language;
    }

    // Use the script file extension if the language is not specified
    final String fileExtension = getFileExtension(script);
    if (!isBlank(fileExtension)) {
      return fileExtension;
    }

    return defaultLanguage;
  }

  private String getScript(final Config config) {
    return config.getStringValue(resourceKey, null);
  }
}
