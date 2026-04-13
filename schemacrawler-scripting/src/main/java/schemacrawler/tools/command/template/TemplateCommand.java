/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommand;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import schemacrawler.tools.scripting.options.LanguageOptions;
import us.fatehi.utility.property.PropertyName;

public final class TemplateCommand
    extends AbstractSchemaCrawlerCommand<LanguageOptions<TemplateLanguageType>> {

  static final PropertyName COMMAND =
      new PropertyName(
          "template", "Process a template file, such as Freemarker, against the database schema");

  public TemplateCommand() {
    super(COMMAND);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    requireNonNull(commandOptions, "No template language provided");
    checkCatalog();

    // Find if the language type is valid, or throw an exception
    final TemplateLanguageType languageType = commandOptions.getLanguage();

    final TemplateRenderer templateRenderer = newTemplateRenderer(languageType);

    final Map<String, Object> context = new HashMap<>();
    context.put("title", outputOptions.getTitle());
    context.put("catalog", getCatalog());
    context.put("er_model", getERModel());
    context.put("identifiers", identifiers);

    templateRenderer.setResourceFilename(commandOptions.getScript());
    templateRenderer.setContext(context);
    templateRenderer.setOutputOptions(outputOptions);

    templateRenderer.execute();
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  private TemplateRenderer newTemplateRenderer(final TemplateLanguageType languageType) {
    try {
      final String templateRendererClassName = languageType.getTemplateRendererClassName();
      final Class<TemplateRenderer> templateRendererClass =
          (Class<TemplateRenderer>) Class.forName(templateRendererClassName);
      final TemplateRenderer templateRenderer = templateRendererClass.newInstance();
      return templateRenderer;
    } catch (final Exception e) {
      throw new InternalRuntimeException(
          "Could not instantiate template renderer for <%s>".formatted(languageType), e);
    }
  }
}
