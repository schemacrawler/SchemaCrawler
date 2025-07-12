/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.template.options;

public enum TemplateLanguageType {
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

  public String getFileExtension() {
    return fileExtension;
  }

  public String getTemplateRendererClassName() {
    return templateRendererClassName;
  }
}
