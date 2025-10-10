/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.scripting.options;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.Optional;
import schemacrawler.tools.executable.CommandOptions;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;

public abstract class LanguageOptions<L extends LanguageType<?>> implements CommandOptions {

  private final L language;
  private final String script;

  public LanguageOptions(final L language, final String script) {
    this.language = requireNonNull(language, "No language provided");
    this.script = requireNotBlank(script, "No script provided");
  }

  public final Optional<InputResource> createInputResource() {
    return InputResourceUtility.createInputResource(script);
  }

  public L getLanguage() {
    return language;
  }

  public String getScript() {
    return script;
  }
}
