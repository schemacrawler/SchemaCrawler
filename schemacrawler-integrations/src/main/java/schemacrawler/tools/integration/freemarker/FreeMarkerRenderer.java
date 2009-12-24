/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.integration.freemarker;


import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.integration.SchemaRenderer;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Main executor for the FreeMarker integration.
 * 
 * @author Sualeh Fatehi
 */
public final class FreeMarkerRenderer
  extends SchemaRenderer
{

  private static final long serialVersionUID = 4029489563062547982L;
  private static final Logger LOGGER = Logger
    .getLogger(FreeMarkerRenderer.class.getName());

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.integration.SchemaRenderer#render(java.lang.String,
   *      schemacrawler.schema.Schema, java.io.Writer)
   */
  @Override
  protected void render(final Connection connection,
                        final String templateName,
                        final Database database,
                        final Writer writer)
    throws ExecutionException
  {
    // Set the file path, in case the template is a file template
    // This allows Velocity to load templates from any directory
    String templateLocation = templateName;
    String templatePath = ".";
    final File templateFilePath = new File(templateLocation);
    if (templateFilePath.exists())
    {
      templatePath = templateFilePath.getAbsoluteFile().getParent();
      templateLocation = templateFilePath.getName();
    }

    try
    {
      // Create a new instance of the configuration
      final Configuration cfg = new Configuration();

      final ClassTemplateLoader ctl = new ClassTemplateLoader(FreeMarkerRenderer.class,
                                                              "/");
      final FileTemplateLoader ftl = new FileTemplateLoader(new File(templatePath));
      final TemplateLoader[] loaders = new TemplateLoader[] {
          ctl, ftl
      };
      final MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
      cfg.setTemplateLoader(mtl);

      cfg.setStrictSyntaxMode(true);
      cfg.setWhitespaceStripping(true);

      cfg.setObjectWrapper(new DefaultObjectWrapper());

      LOGGER.log(Level.INFO, Configuration.getVersionNumber());
      LOGGER.log(Level.INFO, "FreeMarker configuration properties - " + cfg);

      // Create the root hash
      final Map<String, Object> objectMap = new HashMap<String, Object>();
      objectMap.put("database", database);

      // Evaluate the template
      final Template template = cfg.getTemplate(templateLocation);
      template.process(objectMap, writer);

      writer.flush();
      writer.close();
    }
    catch (final IOException e)
    {
      throw new ExecutionException("Could not expand template", e);
    }
    catch (final TemplateException e)
    {
      throw new ExecutionException("Could not expand template", e);
    }
  }

}
