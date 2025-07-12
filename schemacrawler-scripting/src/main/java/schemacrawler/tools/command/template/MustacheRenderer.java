/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.template;

import static us.fatehi.utility.ioresource.InputResourceUtility.createInputResource;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.ioresource.InputResource;

/** Main executor for the Mustache integration. */
public final class MustacheRenderer extends BaseTemplateRenderer {

  @Override
  public void execute() {
    final OutputOptions outputOptions = getOutputOptions();

    final String templateLocation = getResourceFilename();
    final InputResource inputResource =
        createInputResource(templateLocation)
            .orElseThrow(
                () ->
                    new ConfigurationException(
                        String.format("Mustache template not found <%s>", templateLocation)));

    try (final Reader reader = inputResource.openNewInputReader(StandardCharsets.UTF_8)) {
      final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
      final Mustache mustache = mustacheFactory.compile(reader, templateLocation);

      try (final Writer writer = outputOptions.openNewOutputWriter()) {
        // Evaluate the template
        final Map<String, Object> context = getContext();
        mustache.execute(writer, context).flush();
      }
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception rendering FreeMarker template", e);
    }
  }
}
