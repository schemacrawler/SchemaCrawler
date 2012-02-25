/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.integration.velocity;


import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import schemacrawler.schema.Database;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.OutputWriter;

/**
 * Main executor for the Velocity integration.
 * 
 * @author Sualeh Fatehi
 */
public final class VelocityRenderer
  extends BaseExecutable
{

  static final String COMMAND = "velocity";
  private static final Logger LOGGER = Logger.getLogger(VelocityRenderer.class
    .getName());

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
  protected final void executeOn(final Database database,
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
    // http://jakarta.apache.org/velocity/docs/developer-guide.html#
    // Configuring%20Resource%20Loaders
    final String fileResourceLoader = "file";
    final String classpathResourceLoader = "classpath";
    final Properties p = new Properties();
    p.setProperty(RuntimeConstants.RESOURCE_LOADER, fileResourceLoader + ","
                                                    + classpathResourceLoader);
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

    LOGGER.log(Level.INFO,
               "Velocity configuration properties - " + p.toString());

    // Initialize the engine
    ve.init(p);

    // Set the context
    final Context context = new VelocityContext();
    context.put("database", database);

    final Writer writer;
    if (this.writer == null)
    {
      writer = new PrintWriter(new OutputWriter(outputOptions));
    }
    else
    {
      writer = new PrintWriter(new OutputWriter(this.writer));
    }

    // Evaluate the template
    final Template template = ve.getTemplate(templateLocation);
    template.merge(context, writer);

    writer.close();
  }

}
