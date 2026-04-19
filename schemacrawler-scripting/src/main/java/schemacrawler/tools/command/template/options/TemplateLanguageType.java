/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template.options;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.command.template.FreeMarkerRenderer;
import schemacrawler.tools.command.template.MustacheRenderer;
import schemacrawler.tools.command.template.TemplateRenderer;
import schemacrawler.tools.command.template.ThymeleafRenderer;
import schemacrawler.tools.command.template.VelocityRenderer;
import schemacrawler.tools.scripting.options.LanguageType;

public enum TemplateLanguageType implements LanguageType<TemplateLanguageType> {
  unknown(null),
  velocity("vm"),
  freemarker("ftl"),
  mustache("mustache"),
  thymeleaf("thymeleaf");

  private final String fileExtension;

  TemplateLanguageType(final String fileExtension) {
    this.fileExtension = fileExtension;
  }

  /**
   * Instantiates and returns a new {@link TemplateRenderer} for this language type.
   *
   * <p>Renderer classes (e.g. {@code VelocityRenderer}) and their third-party library imports are
   * referenced only inside this method body, never in the enum constructor or static initializer.
   * This ensures that the enum can be loaded (e.g. by picocli scanning enum constants for {@code
   * --help}) without triggering classloading of optional template-engine dependencies that may not
   * be on the classpath in every deployment context.
   *
   * @return a new renderer instance
   * @throws InternalRuntimeException if no renderer is defined for this language type
   */
  public TemplateRenderer newRenderer() {
    return switch (this) {
      case velocity -> new VelocityRenderer();
      case freemarker -> new FreeMarkerRenderer();
      case mustache -> new MustacheRenderer();
      case thymeleaf -> new ThymeleafRenderer();
      case unknown ->
          throw new InternalRuntimeException(
              "Could not instantiate template renderer for <%s>".formatted(this));
    };
  }

  @Override
  public boolean matches(final String languageName) {
    return name().equalsIgnoreCase(languageName)
        || (fileExtension != null && fileExtension.equalsIgnoreCase(languageName));
  }
}
