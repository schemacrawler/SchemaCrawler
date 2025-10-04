/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script.options;

import schemacrawler.tools.options.LanguageType;

public enum ScriptLanguageType implements LanguageType<ScriptLanguageType> {
  unknown(null),
  js("js"),
  ruby("rb"),
  python("py");

  private final String fileExtension;

  ScriptLanguageType(final String fileExtension) {
    this.fileExtension = fileExtension;
  }

  @Override
  public boolean matches(final String languageName) {
    return name().equalsIgnoreCase(languageName) || fileExtension.equalsIgnoreCase(languageName);
  }
}
