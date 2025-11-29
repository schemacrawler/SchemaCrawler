/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template.options;

import schemacrawler.tools.scripting.options.LanguageType;

public enum TemplateLanguageType implements LanguageType<TemplateLanguageType> {
  unknown(null, null),
  velocity("schemacrawler.tools.command.template.VelocityRenderer", "vm"),
  freemarker("schemacrawler.tools.command.template.FreeMarkerRenderer", "ftl"),
  mustache("schemacrawler.tools.command.template.MustacheRenderer", "mustache"),
  thymeleaf("schemacrawler.tools.command.template.ThymeleafRenderer", "thymeleaf");

  private final String fileExtension;
  private final String templateRendererClassName;

  TemplateLanguageType(final String templateRendererClassName, final String fileExtension) {
    this.templateRendererClassName = templateRendererClassName;
    this.fileExtension = fileExtension;
  }

  public String getTemplateRendererClassName() {
    return templateRendererClassName;
  }

  @Override
  public boolean matches(final String languageName) {
    return name().equalsIgnoreCase(languageName) || fileExtension.equalsIgnoreCase(languageName);
  }
}
