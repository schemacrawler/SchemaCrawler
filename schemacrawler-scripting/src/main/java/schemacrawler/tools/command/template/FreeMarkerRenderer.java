/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.options.OutputOptions;

/** Main executor for the FreeMarker integration. */
public final class FreeMarkerRenderer extends BaseTemplateRenderer {

  @Override
  public void execute() {

    final OutputOptions outputOptions = getOutputOptions();

    String templateLocation = getResourceFilename();
    String templatePath = ".";
    final File templateFilePath = new File(templateLocation);
    if (templateFilePath.exists()) {
      templatePath = templateFilePath.getAbsoluteFile().getParent();
      templateLocation = templateFilePath.getName();
    }

    try {
      System.setProperty(
          freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,
          freemarker.log.Logger.LIBRARY_NAME_JUL);

      // Create a new instance of the configuration
      final Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

      final TemplateLoader ctl = new ClassTemplateLoader(FreeMarkerRenderer.class, "/");
      final TemplateLoader ftl = new FileTemplateLoader(new File(templatePath));
      final TemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] {ctl, ftl});
      cfg.setTemplateLoader(mtl);
      cfg.setEncoding(Locale.getDefault(), outputOptions.getInputCharset().name());
      cfg.setWhitespaceStripping(true);

      try (final Writer writer = outputOptions.openNewOutputWriter()) {
        // Evaluate the template
        final Template template = cfg.getTemplate(templateLocation);
        final Map<String, Object> context = getContext();
        template.process(context, writer);
      }
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception rendering FreeMarker template", e);
    }
  }
}
