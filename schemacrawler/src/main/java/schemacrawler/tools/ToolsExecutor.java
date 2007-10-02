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

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.datatext.DataTextFormatterLoader;
import schemacrawler.tools.operation.OperatorLoader;
import schemacrawler.tools.operation.OperatorOptions;
import schemacrawler.tools.schematext.SchemaTextFormatterLoader;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.Config;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
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
   * @see schemacrawler.tools.Executor#execute(schemacrawler.tools.ExecutionContext,
   *      javax.sql.DataSource)
   */
  public void execute(final ExecutionContext executionContext,
                      final DataSource dataSource)
    throws Exception
  {

    CrawlHandler crawlHandler = null;

    final ToolType toolType = executionContext.getToolType();
    switch (toolType)
    {
      case schema_text:
        final SchemaTextOptions schemaTextOptions = (SchemaTextOptions) executionContext
          .getToolOptions();
        crawlHandler = SchemaTextFormatterLoader.load(schemaTextOptions);
        break;
      case operation:
        // Operations are crawl handlers that rely on query execution
        // and result set formatting. Two connections are needed - one
        // for the schema crawling, and another one for exeuting the
        // query. The query is executed once per table, after variables
        // are substituted.
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
        final OperatorOptions operatorOptions = (OperatorOptions) executionContext
          .getToolOptions();
        final DataHandler operationDataHandler = DataTextFormatterLoader
          .load(operatorOptions);
        crawlHandler = OperatorLoader.load(operatorOptions,
                                           connection,
                                           operationDataHandler);
        break;
      case data_text:
        // For data text, the query is executed just once, for the
        // schema. No variable substitutions are made in the query.
        final DataTextFormatOptions dataTextFormatOptions = (DataTextFormatOptions) executionContext
          .getToolOptions();
        final DataHandler dataHandler = DataTextFormatterLoader
          .load(dataTextFormatOptions);
        final QueryExecutor executor = new QueryExecutor(dataSource,
                                                         dataHandler);
        executor.executeSQL(dataTextFormatOptions.getQuery().getQuery());
        break;
    }

    if (toolType != ToolType.data_text)
    {
      final SchemaCrawler crawler = new SchemaCrawler(dataSource,
                                                      additionalConnectionConfiguration,
                                                      crawlHandler);
      crawler.crawl(executionContext.getSchemaCrawlerOptions());
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
