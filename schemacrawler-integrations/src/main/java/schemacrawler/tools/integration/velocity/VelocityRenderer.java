/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.tools.integration.velocity;


import java.io.File;
import java.io.Writer;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.tools.executable.BaseStagedExecutable;
import sf.util.StringFormat;

/**
 * Main executor for the Velocity integration.
 *
 * @author Sualeh Fatehi
 */
public final class VelocityRenderer
  extends BaseStagedExecutable
{

  static final String COMMAND = "velocity";

  private static final Logger LOGGER = Logger
    .getLogger(VelocityRenderer.class.getName());

  private static void setVelocityResourceLoaderProperty(final Properties p,
                                                        final String resourceLoaderName,
                                                        final String resourceLoaderPropertyName,
                                                        final String resourceLoaderPropertyValue)
  {
    p.setProperty(resourceLoaderName + "." + RuntimeConstants.RESOURCE_LOADER
                  + "." + resourceLoaderPropertyName,
                  resourceLoaderPropertyValue);
  }

  public VelocityRenderer()
  {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void executeOn(final Catalog catalog,
                              final Connection connection)
                                throws Exception
  {
    // Set the file path, in case the template is a file template
    // This allows Velocity to load templates from any directory
    String templateLocation = outputOptions.getOutputFormatValue();
    String templatePath = ".";
    final File templateFilePath = new File(templateLocation);
    if (templateFilePath.exists())
    {
      templatePath = templatePath + ","
                     + templateFilePath.getAbsoluteFile().getParent();
      templateLocation = templateFilePath.getName();
    }

    // Create a new instance of the engine
    final VelocityEngine ve = new VelocityEngine();
    ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new JdkLogChute());

    // Set up Velocity resource loaders for loading from the
    // classpath, as well as the file system
    // http://velocity.apache.org/engine/releases/velocity-1.7/developer-guide.html#Configuring_Resource_Loaders
    final String fileResourceLoader = "file";
    final String classpathResourceLoader = "classpath";
    final Properties p = new Properties();
    p.setProperty(RuntimeConstants.RESOURCE_LOADER,
                  fileResourceLoader + "," + classpathResourceLoader);
    setVelocityResourceLoaderProperty(p,
                                      classpathResourceLoader,
                                      "class",
                                      ClasspathResourceLoader.class.getName());
    setVelocityResourceLoaderProperty(p,
                                      fileResourceLoader,
                                      "class",
                                      FileResourceLoader.class.getName());
    setVelocityResourceLoaderProperty(p,
                                      fileResourceLoader,
                                      "path",
                                      templatePath);

    LOGGER.log(Level.CONFIG,
               new StringFormat("Velocity configuration properties, %s",
                                p.toString()));

    ve.init(p);

    final Context context = new VelocityContext();
    context.put("catalog", catalog);

    try (final Writer writer = outputOptions.openNewOutputWriter();)
    {
      final String templateEncoding = outputOptions.getInputCharset().name();
      LOGGER.log(Level.INFO,
                 new StringFormat("Reading Velocity template, %s, with encoding \"%s\"",
                                  templateLocation,
                                  templateEncoding));
      final Template template = ve.getTemplate(templateLocation,
                                               templateEncoding);
      template.merge(context, writer);
    }
    catch (final ResourceNotFoundException e)
    {
      throw new SchemaCrawlerCommandLineException("No template specified", e);
    }

  }

}
