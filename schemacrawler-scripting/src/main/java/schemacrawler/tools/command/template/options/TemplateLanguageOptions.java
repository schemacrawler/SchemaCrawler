/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template.options;

import schemacrawler.tools.scripting.options.LanguageOptions;

public final class TemplateLanguageOptions extends LanguageOptions<TemplateLanguageType> {

  public TemplateLanguageOptions(final TemplateLanguageType language, final String script) {
    super(language, script);
  }
}
