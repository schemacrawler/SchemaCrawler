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

package schemacrawler.tools.text.operation;


import java.sql.Connection;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.Executable;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.OutputOptions;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class OperationExecutable
  extends Executable<OperationOptions>
{

  /**
   * {@inheritDoc}
   * <p>
   * Operations are crawl handlers that rely on query execution and
   * result set formatting. A connection is needed for the schema
   * crawling, and for executing the query. "Query-over" queries are
   * executed once per table, after variables are substituted. Other
   * queries are executed just once.
   * 
   * @see schemacrawler.tools.Executable#execute(Connection)
   */
  @Override
  public void execute(final Connection connection)
    throws ExecutionException
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    initialize();

    try
    {
      final SchemaCrawler crawler = new DatabaseSchemaCrawler(connection);
      crawler.crawl(schemaCrawlerOptions, new OperationHandler(toolOptions,
                                                               connection));
    }
    catch (final SchemaCrawlerException e)
    {
      throw new ExecutionException("Could not execute operation", e);
    }
  }

  @Override
  public void initialize(final String command,
                         final Config config,
                         final SchemaCrawlerOptions schemaCrawlerOptions,
                         final OutputOptions outputOptions)
    throws ExecutionException
  {

    Operation operation;
    OperationOptions operationOptions;
    try
    {
      operation = Operation.valueOf(command);
      operationOptions = new OperationOptions(config, outputOptions, operation);
    }
    catch (final IllegalArgumentException e)
    {
      final String queryName = command;
      operationOptions = new OperationOptions(config, outputOptions, queryName);
    }
    toolOptions = operationOptions;
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

}
