/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.tools.operation;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.Query;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.datatext.DataToolsExecutable;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class OperationExecutable
  extends Executable<OperationOptions>
{

  private static final Logger LOGGER = Logger
    .getLogger(OperationExecutable.class.getName());

  /**
   * {@inheritDoc}
   * <p>
   * Operations are crawl handlers that rely on query execution and
   * result set formatting. Two connections are needed - one for the
   * schema crawling, and another one for executing the query. The query
   * is executed once per table, after variables are substituted.
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {

    final Connection connection;
    try
    {
      connection = dataSource.getConnection();
    }
    catch (final SQLException e)
    {
      final String errorMessage = e.getMessage();
      LOGGER.log(Level.WARNING, "Cannot obtain a connection: " + errorMessage);
      throw new SchemaCrawlerException(errorMessage, e);
    }
    final DataHandler operationDataHandler = DataToolsExecutable
      .createDataHandler(toolOptions);

    CrawlHandler handler = null;
    final Operation operation = toolOptions.getOperation();
    Query query;
    if (operation == Operation.queryover)
    {
      query = toolOptions.getQuery();
    }
    else
    {
      query = operation.getQuery();
    }

    final OutputFormat outputFormatType = toolOptions.getOutputOptions()
      .getOutputFormat();
    if (outputFormatType == OutputFormat.html)
    {
      handler = new OperatorHTMLOutput(toolOptions,
                                       query,
                                       connection,
                                       operationDataHandler);
    }
    else
    {
      handler = new OperatorTextOutput(toolOptions,
                                       query,
                                       connection,
                                       operationDataHandler);
    }

    final SchemaCrawler crawler = new SchemaCrawler(dataSource, handler);
    crawler.crawl(schemaCrawlerOptions);
  }
}
