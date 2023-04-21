/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
