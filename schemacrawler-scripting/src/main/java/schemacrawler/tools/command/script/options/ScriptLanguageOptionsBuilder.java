/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script.options;

import java.util.EnumSet;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.LanguageOptionsBuilder;

public final class ScriptLanguageOptionsBuilder
    extends LanguageOptionsBuilder<ScriptLanguageType, ScriptOptions> {

  public static ScriptLanguageOptionsBuilder builder() {
    return new ScriptLanguageOptionsBuilder();
  }

  private Config config;

  private ScriptLanguageOptionsBuilder() {
    super("script-language", "script", ScriptLanguageType.unknown);
    config = new Config();
  }

  @Override
  public ScriptLanguageOptionsBuilder fromConfig(final Config config) {
    super.fromConfig(config);
    this.config = new Config(config);
    return this;
  }

  @Override
  public ScriptLanguageOptionsBuilder fromOptions(final ScriptOptions options) {
    super.fromOptions(options);
    if (options != null) {
      config = options.getConfig();
    }
    return this;
  }

  @Override
  public ScriptOptions toOptions() {
    return new ScriptOptions(getLanguage(), getScript(), config);
  }

  @Override
  protected ScriptLanguageType languageFromString(final String languageName) {
    for (final ScriptLanguageType scriptLanguageType :
        EnumSet.complementOf(EnumSet.of(ScriptLanguageType.unknown))) {
      if (scriptLanguageType.matches(languageName)) {
        return scriptLanguageType;
      }
    }
    return ScriptLanguageType.unknown;
  }
}
