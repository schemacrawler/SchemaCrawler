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

package schemacrawler.tools.operation;


import java.sql.Connection;

import schemacrawler.crawl.DatabaseSchemaCrawler;
import schemacrawler.execute.DataHandler;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.tools.Executable;
import schemacrawler.tools.datatext.DataToolsExecutable;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class OperationExecutable
  extends Executable<OperationOptions>
{

  public OperationExecutable()
  {
    this(OperationExecutable.class.getSimpleName());
  }

  public OperationExecutable(final String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Operations are crawl handlers that rely on query execution and
   * result set formatting. Two connections are needed - one for the
   * schema crawling, and another one for executing the query. The query
   * is executed once per table, after variables are substituted.
   * 
   * @see schemacrawler.tools.Executable#execute(Connection)
   */
  @Override
  public void execute(final Connection connection)
    throws Exception
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    initialize();

    final DataHandler operationDataHandler = DataToolsExecutable
      .createDataHandler(toolOptions);

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

    final SchemaCrawler crawler = new DatabaseSchemaCrawler(connection);
    crawler.crawl(schemaCrawlerOptions,
                  new OperationFormatter(toolOptions,
                                         query,
                                         connection,
                                         operationDataHandler));
  }

}
