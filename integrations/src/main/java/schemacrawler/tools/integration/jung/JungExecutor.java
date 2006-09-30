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

package schemacrawler.tools.integration.jung;


import java.awt.Dimension;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

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
import edu.uci.ics.jung.graph.Graph;

/**
 * Main executor for the JUNG integration.
 * 
 * @author Sualeh Fatehi
 */
public final class JungExecutor
  implements SchemaCrawlerExecutor
{

  private static final int DEFAULT_IMAGE_WIDTH = 600;

  private Dimension getSize(final String dimensions)
  {
    final String[] sizes = dimensions.split("x");
    try
    {
      final int width = Integer.parseInt(sizes[0]);
      final int height = Integer.parseInt(sizes[1]);
      return new Dimension(width, height);
    }
    catch (final NumberFormatException e)
    {
      return new Dimension(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_WIDTH);
    }
  }

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
    final File outputFile = schemaTextOptions.getOutputOptions()
      .getOutputFile();
    final Dimension size = getSize(schemaTextOptions.getOutputOptions()
      .getOutputFormatValue());
    final Schema schema = SchemaCrawler.getSchema(dataSource,
                                                  additionalConfiguration,
                                                  schemaTextOptions
                                                    .getSchemaTextDetailType()
                                                    .mapToInfoLevel(),
                                                  schemaCrawlerOptions);
    final Graph graph = JungUtil.makeSchemaGraph(schema);
    JungUtil.saveGraphJpeg(graph, outputFile, size);
  }

}
