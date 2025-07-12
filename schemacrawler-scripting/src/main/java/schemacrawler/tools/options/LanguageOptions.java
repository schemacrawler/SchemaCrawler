/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.options;

import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.ioresource.InputResourceUtility.createInputResource;

import java.util.Optional;

import schemacrawler.tools.executable.CommandOptions;
import us.fatehi.utility.ioresource.InputResource;

public abstract class LanguageOptions implements CommandOptions {

  private final String language;
  private final String script;

  public LanguageOptions(final String language, final String script) {
    this.language = requireNotBlank(language, "No language provided");
    this.script = requireNotBlank(script, "No script provided");
  }

  public String getLanguage() {
    return language;
  }

  public final Optional<InputResource> getInputResource() {
    return createInputResource(script);
  }

  public String getScript() {
    return script;
  }
}
