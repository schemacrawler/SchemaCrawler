/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template.options;

import java.util.EnumSet;
import schemacrawler.tools.scripting.options.LanguageOptionsBuilder;

public final class TemplateLanguageOptionsBuilder
    extends LanguageOptionsBuilder<TemplateLanguageType, TemplateLanguageOptions> {

  public static TemplateLanguageOptionsBuilder builder() {
    return new TemplateLanguageOptionsBuilder();
  }

  private TemplateLanguageOptionsBuilder() {
    super("templating-language", "template", TemplateLanguageType.unknown);
  }

  @Override
  public TemplateLanguageOptions toOptions() {
    return new TemplateLanguageOptions(getLanguage(), getScript());
  }

  @Override
  protected TemplateLanguageType languageFromString(final String languageName) {
    for (final TemplateLanguageType scriptLanguageType :
        EnumSet.complementOf(EnumSet.of(TemplateLanguageType.unknown))) {
      if (scriptLanguageType.matches(languageName)) {
        return scriptLanguageType;
      }
    }
    return TemplateLanguageType.unknown;
  }
}
