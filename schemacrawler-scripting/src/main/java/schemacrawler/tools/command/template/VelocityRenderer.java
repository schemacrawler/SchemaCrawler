/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.options.OutputOptions;

public final class VelocityRenderer extends BaseTemplateRenderer {

  private static void setVelocityResourceLoaderProperty(
      final Properties p,
      final String resourceLoaderName,
      final String resourceLoaderPropertyName,
      final String resourceLoaderPropertyValue) {
    p.setProperty(
        resourceLoaderName
            + "."
            + RuntimeConstants.RESOURCE_LOADER
            + "."
            + resourceLoaderPropertyName,
        resourceLoaderPropertyValue);
  }

  @Override
  public void execute() {

    final OutputOptions outputOptions = getOutputOptions();

    // Set the file path, in case the template is a file template
    // This allows Velocity to load templates from any directory
    String templateLocation = getResourceFilename();
    String templatePath = ".";
    final Path templateFilePath = Path.of(templateLocation);
    if (Files.exists(templateFilePath)) {
      final Path templateFileParentPath = templateFilePath.normalize().getParent();
      if (templateFileParentPath != null) {
        templatePath = templatePath + "," + templateFileParentPath.toAbsolutePath();
        templateLocation = templateFilePath.getFileName().toString();
      }
    }

    try {
      // Create a new instance of the engine
      final VelocityEngine ve = new VelocityEngine();

      // Set up Velocity resource loaders for loading from the
      // classpath, as well as the file system
      // http://velocity.apache.org/engine/releases/velocity-1.7/developer-guide.html#Configuring_Resource_Loaders
      final String fileResourceLoader = "file";
      final String classpathResourceLoader = "classpath";
      final Properties p = new Properties();
      p.setProperty(
          RuntimeConstants.RESOURCE_LOADER, fileResourceLoader + "," + classpathResourceLoader);
      setVelocityResourceLoaderProperty(
          p, classpathResourceLoader, "class", ClasspathResourceLoader.class.getName());
      setVelocityResourceLoaderProperty(
          p, fileResourceLoader, "class", FileResourceLoader.class.getName());
      setVelocityResourceLoaderProperty(p, fileResourceLoader, "path", templatePath);

      ve.init(p);

      final Context context = new VelocityContext(getContext());

      try (final Writer writer = outputOptions.openNewOutputWriter()) {
        final String templateEncoding = outputOptions.getInputCharset().name();
        final Template template = ve.getTemplate(templateLocation, templateEncoding);
        template.merge(context, writer);
      } catch (final ResourceNotFoundException e) {
        throw new ConfigurationException("Apache Velocity template not found", e);
      }
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Exception rendering Apache Velocity template", e);
    }
  }
}
