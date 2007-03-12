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

package schemacrawler.tools;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.Executor;
import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.main.Config;
import schemacrawler.main.Options;
import schemacrawler.tools.datatext.DataTextFormatterLoader;
import schemacrawler.tools.operation.OperatorLoader;
import schemacrawler.tools.schematext.SchemaTextFormatterLoader;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author sfatehi
 */
public class ToolsExecutor
  implements Executor
{

  private static final Logger LOGGER = Logger.getLogger(ToolsExecutor.class
    .getName());

  private Config additionalConnectionConfiguration = new Config();

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
    if (toolType == ToolType.SCHEMA_TEXT)
    {
      crawlHandler = SchemaTextFormatterLoader.load(options
        .getSchemaTextOptions());
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
    }
    if (toolType == ToolType.DATA_TEXT)
    {
      final QueryExecutor executor = new QueryExecutor(dataSource, dataHandler);
      executor.executeSQL(options.getQuery());
    }
    else
    {
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      additionalConnectionConfiguration,
                                                      crawlHandler);
      crawler.crawl(options.getSchemaCrawlerOptions());
    }
  }

  /**
   * Set additional connection (data source) specific configuration, if
   * needed.
   * 
   * @param additionalConnectionConfiguration
   *        Additional connection configuration.
   */
  public void setAdditionalConnectionConfiguration(final Config additionalConnectionConfiguration)
  {
    if (additionalConnectionConfiguration != null)
    {
      this.additionalConnectionConfiguration = additionalConnectionConfiguration;
    }
  }

}
