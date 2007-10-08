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
package schemacrawler.tools.integration;


import java.io.Writer;

import javax.sql.DataSource;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.schema.Schema;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public final class TemplatedSchemaCrawlerExecutor
  implements SchemaCrawlerExecutor
{

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @param readmeResource
   *        Resource location for readme file.
   * @param schemaRenderer
   *        Template renderer
   * @throws Exception
   *         On an exception
   */
  public static void templatingToolMain(final String[] args,
                                        final String readmeResource,
                                        final TemplatedSchemaRenderer schemaRenderer)
    throws Exception
  {
    IntegrationUtility
      .integrationToolMain(args,
                           readmeResource,
                           new TemplatedSchemaCrawlerExecutor(schemaRenderer));
  }

  private final TemplatedSchemaRenderer templatedRenderer;

  /**
   * Create a new instance of the executor for a template renderer.
   * 
   * @param templatedRenderer
   *        Template renderer.
   */
  public TemplatedSchemaCrawlerExecutor(final TemplatedSchemaRenderer templatedRenderer)
  {
    if (templatedRenderer == null)
    {
      throw new IllegalArgumentException("A TemplatedSchemaRenderer is required");
    }
    this.templatedRenderer = templatedRenderer;
  }

  /**
   * Executes main functionality.
   * 
   * @param schemaCrawlerOptions
   *        SchemaCrawler options
   * @param schemaTextOptions
   *        Text output options
   * @param dataSource
   *        Data-source
   * @throws Exception
   *         On an exception
   */
  public void execute(final SchemaCrawlerOptions schemaCrawlerOptions,
                      final SchemaTextOptions schemaTextOptions,
                      final DataSource dataSource)
    throws Exception
  {
    // Get the entire schema at once, since we need to use this to
    // render the template
    final Schema schema = SchemaCrawler.getSchema(dataSource, schemaTextOptions
      .getSchemaTextDetailType().mapToInfoLevel(), schemaCrawlerOptions);
    final Writer writer = schemaTextOptions.getOutputOptions()
      .openOutputWriter();
    final String templateName = schemaTextOptions.getOutputOptions()
      .getOutputFormatValue();
    templatedRenderer.renderTemplate(templateName, schema, writer);
  }

}
