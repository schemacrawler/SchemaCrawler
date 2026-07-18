/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.okf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;
import us.fatehi.utility.string.StringFormat;

/** Renders FreeMarker templates and writes pages to the output context. */
public final class OkfTemplateRenderer {

  private static final Logger LOGGER = Logger.getLogger(OkfTemplateRenderer.class.getName());

  private final Configuration configuration;
  private final BundleDirectoryOutput outputDirectory;

  public OkfTemplateRenderer(final BundleDirectoryOutput outputDirectory) {
    this.outputDirectory = requireNonNull(outputDirectory, "No output directory provided");
    configuration = createFreemarkerConfiguration();
  }

  public void writeTemplate(
      final String templateName, final Map<String, Object> model, final String relativePath) {
    try (final Writer writer =
        outputDirectory.toWrite(relativePath).openNewOutputWriter(UTF_8, false)) {
      final Template template = configuration.getTemplate(templateName);
      template.process(model, writer);
    } catch (final IOException | TemplateException e) {
      throw new ExecutionRuntimeException(
          new StringFormat("Could not render template <%s>", templateName).get(), e);
    }
  }

  private Configuration createFreemarkerConfiguration() {
    final class OkfTemplateLoader implements TemplateLoader {
      @Override
      public void closeTemplateSource(final Object templateSource) {
        // nothing to close; readers are closed by FreeMarker
      }

      @Override
      public Object findTemplateSource(final String name) {
        requireNotBlank(name, "Template name not provided");
        final String templatePath = "templates/" + name;
        final Optional<InputResource> hasInputResource =
            InputResourceUtility.createInputResource(templatePath);
        if (hasInputResource.isEmpty()) {
          return null;
        }
        final InputResource inputResource = hasInputResource.get();
        return inputResource;
      }

      @Override
      public long getLastModified(final Object templateSource) {
        return -1;
      }

      @Override
      public Reader getReader(final Object templateSource, final String encoding)
          throws IOException {
        requireNonNull(templateSource, "No template source provided");
        if (!(templateSource instanceof final InputResource inputResource)) {
          throw new TemplateNotFoundException(
              templateSource.toString(), null, "Template input resource not found");
        }
        LOGGER.log(Level.FINE, new StringFormat("Reading FreeMarker template <%s>", inputResource));
        return inputResource.openNewInputReader(UTF_8);
      }
    }

    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
    cfg.setTemplateLoader(new OkfTemplateLoader());
    cfg.setDefaultEncoding("UTF-8");
    // Localization is handled with the helper messages
    cfg.setLocalizedLookup(false);
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(true);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setBooleanFormat("c");
    return cfg;
  }
}
