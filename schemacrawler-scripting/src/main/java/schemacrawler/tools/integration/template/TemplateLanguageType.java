/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.template;


import java.util.EnumSet;

public enum TemplateLanguageType
{
  unknown(null, null),
  velocity("schemacrawler.tools.integration.velocity.VelocityRenderer", "vm"),
  freemarker("schemacrawler.tools.integration.freemarker.FreeMarkerRenderer",
             "ftl"),
  mustache("schemacrawler.tools.integration.mustache.MustacheRenderer",
           "mustache"),
  thymeleaf("schemacrawler.tools.integration.thymeleaf.ThymeleafRenderer",
            "thymeleaf");

  /**
   * Find the enumeration value corresponding to the string.
   *
   * @param extension Sort sequence code.
   * @return Enumeration value
   */
  public static TemplateLanguageType valueOfFromExtension(final String extension)
  {
    for (final TemplateLanguageType type : EnumSet.complementOf(EnumSet.of(
      TemplateLanguageType.unknown)))
    {
      if (type.getFileExtension().equalsIgnoreCase(extension))
      {
        return type;
      }
    }
    return unknown;
  }

  private final String fileExtension;
  private final String templateRendererClassName;

  TemplateLanguageType(final String templateRendererClassName,
                       final String fileExtension)
  {
    this.templateRendererClassName = templateRendererClassName;
    this.fileExtension = fileExtension;
  }

  public String getFileExtension()
  {
    return fileExtension;
  }

  public String getTemplateRendererClassName()
  {
    return templateRendererClassName;
  }

}
