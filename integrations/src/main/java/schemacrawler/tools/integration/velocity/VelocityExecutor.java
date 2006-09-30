/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import schemacrawler.Options;
import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.schema.Schema;
import schemacrawler.tools.SchemaCrawlerExecutor;
import schemacrawler.tools.ToolType;
import schemacrawler.tools.datatext.DataTextFormatterLoader;
import schemacrawler.tools.operation.OperatorLoader;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * Main executor for the Velocity integration.
 * 
 * @author Sualeh Fatehi
 */
public class VelocityExecutor
  implements SchemaCrawlerExecutor
{

  private static final Logger LOGGER = Logger.getLogger(VelocityExecutor.class
    .getName());

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.Executor#execute(schemacrawler.Options,
   *      javax.sql.DataSource)
   */
  public void execute(final Options options, final DataSource dataSource,
                      final Properties additionalConfiguration)
    throws Exception
  {
    DataHandler dataHandler = null;
    CrawlHandler crawlHandler = null;

    final ToolType toolType = options.getToolType();
    final SchemaCrawlerOptions schemaCrawlerOptions = options
      .getSchemaCrawlerOptions();
    final SchemaTextOptions schemaTextOptions = options.getSchemaTextOptions();

    if (toolType == ToolType.SCHEMA_TEXT)
    {
      execute(schemaCrawlerOptions, schemaTextOptions, dataSource,
              additionalConfiguration);
    }
    else
    {

      // For operations and single queries
      dataHandler = DataTextFormatterLoader.load(options
        .getDataTextFormatOptions());
      if (toolType == ToolType.OPERATION)
      {
        // Operations are crawl handlers that rely on
        // query execution and result set formatting
        final Connection connection;
        try
        {
          connection = dataSource.getConnection();
        }
        catch (final SQLException e)
        {
          throw new SchemaCrawlerException("Cannot obtain a connection", e);
        }
        crawlHandler = OperatorLoader.load(options.getOperatorOptions(),
                                           connection, dataHandler);
      }
      if (toolType == ToolType.DATA_TEXT)
      {
        final QueryExecutor executor = new QueryExecutor(dataSource,
                                                         dataHandler);
        executor.executeSQL(options.getQuery());
      }
      else if (toolType == ToolType.OPERATION)
      {
        final SchemaCrawler crawler = new SchemaCrawler(
                                                        dataSource,
                                                        additionalConfiguration,
                                                        crawlHandler);
        crawler.crawl(schemaCrawlerOptions);
      }
    }
  }

  /**
   * Executes main functionality.
   * 
   * @see {@link VelocityExecutor#execute(Options, DataSource)}
   * @param schemaCrawlerOptions
   *          SchemaCrawler options
   * @param schemaTextOptions
   *          Text output options
   * @param dataSource
   *          Datasource
   * @throws Exception
   *           On an exception
   */
  public void execute(final SchemaCrawlerOptions schemaCrawlerOptions,
                      final SchemaTextOptions schemaTextOptions,
                      final DataSource dataSource,
                      final Properties additionalConfiguration)
    throws Exception
  {
    // Get the entire schema at once, since we need to use this to
    // render
    // the velocity template
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  additionalConfiguration,
                                                  schemaTextOptions
                                                    .getSchemaTextDetailType()
                                                    .mapToInfoLevel(),
                                                  schemaCrawlerOptions);
    final Writer writer = schemaTextOptions.getOutputOptions()
      .getOutputWriter();
    final String templateName = schemaTextOptions.getOutputOptions()
      .getOutputFormatValue();
    renderTemplate(templateName, schema, writer);
  }

  private static void renderTemplate(final String templateName,
                                     final Schema schema, final Writer writer)
    throws Exception
  {
    // Set the file path, in case the template is a file template
    // This allows Velocity to load templates from any directory
    String templateLocation = templateName;
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

    // Set up Velocity resource loaders for loading from the classpath,
    // as well as the file system
    // http://jakarta.apache.org/velocity/docs/developer-guide.html#Configuring%20Resource%20Loaders
    final String fileResourceLoader = "file";
    final String classpathResourceLoader = "classpath";
    final Properties p = new Properties();
    p.setProperty(RuntimeConstants.RESOURCE_LOADER, fileResourceLoader + ","
                                                    + classpathResourceLoader);
    setVelocityResourceLoaderProperty(p, classpathResourceLoader, "class",
                                      ClasspathResourceLoader.class.getName());
    setVelocityResourceLoaderProperty(p, fileResourceLoader, "class",
                                      FileResourceLoader.class.getName());
    setVelocityResourceLoaderProperty(p, fileResourceLoader, "path",
                                      templatePath);

    LOGGER.log(Level.INFO, "Velocity configuration properties - "
                           + p.toString());

    // Initialize the engine
    ve.init(p);

    // Set the context
    final VelocityContext context = new VelocityContext();
    context.put("schema", schema);

    // Evaluate the template
    final Template template = ve.getTemplate(templateLocation);
    template.merge(context, writer);

    writer.flush();
  }

  private static void setVelocityResourceLoaderProperty(
                                                        final Properties p,
                                                        final String resourceLoaderName,
                                                        final String resourceLoaderPropertyName,
                                                        final String resourceLoaderPropertyValue)
  {
    p.setProperty(resourceLoaderName + "." + RuntimeConstants.RESOURCE_LOADER
                  + "." + resourceLoaderPropertyName,
                  resourceLoaderPropertyValue);
  }

}
