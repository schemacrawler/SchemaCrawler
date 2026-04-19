/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.util.HashMap;
import java.util.Map;
import schemacrawler.schema.Catalog;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommand;
import schemacrawler.tools.command.script.CrawlInfoSupport;
import schemacrawler.tools.command.script.ScriptSupport;
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

    final TemplateRenderer templateRenderer = languageType.newRenderer();

    // Set up the context
    final String title = title();
    final Catalog catalog = getCatalog();
    final Map<String, Object> context = new HashMap<>();
    context.put("title", title);
    context.put("catalog", catalog);
    context.put("er_model", getERModel());
    context.put("support", new ScriptSupport());
    context.put("crawl_info", new CrawlInfoSupport(catalog.getCrawlInfo()));

    templateRenderer.setResourceFilename(commandOptions.getScript());
    templateRenderer.setContext(context);
    templateRenderer.setOutputOptions(outputOptions);

    templateRenderer.execute();
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  private String title() {
    final String requestedTitle = outputOptions.getTitle();
    if (!isBlank(requestedTitle)) {
      return requestedTitle;
    }
    return "Database Schema";
  }
}
