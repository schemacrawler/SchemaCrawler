/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.template.options;

import schemacrawler.tools.options.LanguageOptionsBuilder;

public final class TemplateLanguageOptionsBuilder
    extends LanguageOptionsBuilder<TemplateLanguageOptions> {

  public static TemplateLanguageOptionsBuilder builder() {
    return new TemplateLanguageOptionsBuilder();
  }

  private TemplateLanguageOptionsBuilder() {
    super("templating-language", "template", TemplateLanguageType.unknown.name());
  }

  @Override
  public TemplateLanguageOptions toOptions() {
    return new TemplateLanguageOptions(getLanguage(), getScript());
  }
}
