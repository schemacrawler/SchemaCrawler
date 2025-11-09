/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script.options;

import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.scripting.options.LanguageOptions;

public class ScriptOptions extends LanguageOptions<ScriptLanguageType> {

  private final Config config;

  public ScriptOptions(
      final ScriptLanguageType language, final String script, final Config config) {
    super(language, script);
    this.config = ConfigUtility.fromConfig(config);
  }

  public Config getConfig() {
    return ConfigUtility.fromConfig(config);
  }
}
