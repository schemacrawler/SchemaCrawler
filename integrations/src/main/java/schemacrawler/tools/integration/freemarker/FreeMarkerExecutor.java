/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.main.Options;
import schemacrawler.schema.Schema;
import schemacrawler.tools.ToolType;
import schemacrawler.tools.datatext.DataTextFormatterLoader;
import schemacrawler.tools.integration.SchemaCrawlerExecutor;
import schemacrawler.tools.operation.OperatorLoader;
import schemacrawler.tools.schematext.SchemaTextOptions;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * Main executor for the FreeMarker integration.
 * 
 * @author Sualeh Fatehi
 */
public class FreeMarkerExecutor
  implements SchemaCrawlerExecutor
{

  private static final Logger LOGGER = Logger
    .getLogger(FreeMarkerExecutor.class.getName());

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.Executor#execute(schemacrawler.main.Options,
   *      javax.sql.DataSource)
   */
  public void execute(final Options options, final DataSource dataSource)
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
      execute(schemaCrawlerOptions, schemaTextOptions, dataSource);
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
          final String errorMessage = e.getMessage();
          LOGGER.log(Level.WARNING, "Cannot obtain a connection: "
                                    + errorMessage);
          throw new SchemaCrawlerException(errorMessage, e);
        }
        crawlHandler = OperatorLoader.load(options.getOperatorOptions(),
                                           connection,
                                           dataHandler);
      }
      if (toolType == ToolType.DATA_TEXT)
      {
        final QueryExecutor executor = new QueryExecutor(dataSource,
                                                         dataHandler);
        executor.executeSQL(options.getQuery());
      }
      else if (toolType == ToolType.OPERATION)
      {
        final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                        crawlHandler);
        crawler.crawl(schemaCrawlerOptions);
      }
    }
  }

  /**
   * Executes main functionality.
   * 
   * @param schemaCrawlerOptions
   *        SchemaCrawler options
   * @param schemaTextOptions
   *        Text output options
   * @param dataSource
   *        Datasource
   * @throws Exception
   *         On an exception
   */
  public void execute(final SchemaCrawlerOptions schemaCrawlerOptions,
                      final SchemaTextOptions schemaTextOptions,
                      final DataSource dataSource)
    throws Exception
  {
    // Get the entire schema at once, since we need to use this to
    // render
    // the velocity template
    final Schema schema = SchemaCrawler.getSchema(dataSource, schemaTextOptions
      .getSchemaTextDetailType().mapToInfoLevel(), schemaCrawlerOptions);
    final Writer writer = schemaTextOptions.getOutputOptions()
      .openOutputWriter();
    final String templateName = schemaTextOptions.getOutputOptions()
      .getOutputFormatValue();
    renderTemplate(templateName, schema, writer);
  }

  private static void renderTemplate(final String templateName,
                                     final Schema schema,
                                     final Writer writer)
    throws Exception
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

    // Create a new instance of the configuration
    final Configuration cfg = new Configuration();

    final ClassTemplateLoader ctl = new ClassTemplateLoader(FreeMarkerExecutor.class,
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
    final Map objectMap = new HashMap();
    objectMap.put("schema", schema);

    // Evaluate the template
    final Template template = cfg.getTemplate(templateLocation);
    template.process(objectMap, writer);

    writer.flush();

  }

}
