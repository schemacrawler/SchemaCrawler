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

import schemacrawler.crawl.InformationSchemaViews;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.main.SchemaCrawlerMain;
import schemacrawler.schema.Schema;
import schemacrawler.tools.ExecutionContext;
import schemacrawler.tools.Executor;
import schemacrawler.tools.ToolType;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineUtility;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public final class TemplatedSchemaCrawlerExecutor
  implements Executor
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
                                        final TemplatedSchemaRenderer schemaRenderer)
    throws Exception
  {
    CommandLineUtility.checkForHelp(args,
                                    "/schemacrawler-templating-readme.txt");
    CommandLineUtility.setLogLevel(args);

    SchemaCrawlerMain
      .schemacrawler(args, new TemplatedSchemaCrawlerExecutor(schemaRenderer));
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

  public void execute(final ExecutionContext executionContext,
                      final DataSource dataSource)
    throws Exception
  {
    if (executionContext == null
        || executionContext.getToolType() != ToolType.schema_text)
    {
      throw new IllegalArgumentException("Bad execution context specified");
    }

    final SchemaCrawlerOptions schemaCrawlerOptions = executionContext
      .getSchemaCrawlerOptions();
    final SchemaTextOptions schemaTextOptions = (SchemaTextOptions) executionContext
      .getToolOptions();
    final InformationSchemaViews informationSchemaViews = executionContext
      .getInformationSchemaViews();

    // Get the entire schema at once, since we need to use this to
    // render the template
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  informationSchemaViews,
                                                  schemaTextOptions
                                                    .getSchemaTextDetailType()
                                                    .mapToInfoLevel(),
                                                  schemaCrawlerOptions);
    final Writer writer = schemaTextOptions.getOutputOptions()
      .openOutputWriter();
    final String templateName = schemaTextOptions.getOutputOptions()
      .getOutputFormatValue();
    templatedRenderer.renderTemplate(templateName, schema, writer);
  }

}
