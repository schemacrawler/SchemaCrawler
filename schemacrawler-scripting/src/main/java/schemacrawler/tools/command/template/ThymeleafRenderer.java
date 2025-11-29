/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template;

import java.io.Writer;
import java.nio.charset.Charset;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.options.OutputOptions;

/** Main executor for the Thymeleaf integration. */
public final class ThymeleafRenderer extends BaseTemplateRenderer {

  private static ITemplateResolver configure(
      final AbstractConfigurableTemplateResolver templateResolver, final Charset inputEncoding) {
    templateResolver.setCharacterEncoding(inputEncoding.name());
    templateResolver.setTemplateMode("HTML5");
    return templateResolver;
  }

  @Override
  public void execute() {
    final OutputOptions outputOptions = getOutputOptions();

    try {
      final Context context = new Context();
      context.setVariables(getContext());

      final TemplateEngine templateEngine = new TemplateEngine();
      final Charset inputCharset = outputOptions.getInputCharset();

      final FileTemplateResolver fileResolver = new FileTemplateResolver();
      fileResolver.setCheckExistence(true);
      templateEngine.addTemplateResolver(configure(fileResolver, inputCharset));

      final ClassLoaderTemplateResolver classpathResolver = new ClassLoaderTemplateResolver();
      classpathResolver.setCheckExistence(true);
      templateEngine.addTemplateResolver(configure(classpathResolver, inputCharset));

      final UrlTemplateResolver urlResolver = new UrlTemplateResolver();
      urlResolver.setCheckExistence(true);
      templateEngine.addTemplateResolver(configure(urlResolver, inputCharset));

      final String templateLocation = getResourceFilename();
      try (final Writer writer = outputOptions.openNewOutputWriter()) {
        templateEngine.process(templateLocation, context, writer);
      }
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception rendering Thymeleaf template", e);
    }
  }
}
