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

package schemacrawler.tools.operation;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.tools.Executable;
import schemacrawler.tools.datatext.DataTextFormatterLoader;

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
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
    // Operations are crawl handlers that rely on query execution
    // and result set formatting. Two connections are needed - one
    // for the schema crawling, and another one for executing the
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
      LOGGER.log(Level.WARNING, "Cannot obtain a connection: " + errorMessage);
      throw new SchemaCrawlerException(errorMessage, e);
    }
    final DataHandler operationDataHandler = DataTextFormatterLoader
      .load(toolOptions);
    final CrawlHandler crawlHandler = OperatorLoader.load(toolOptions,
                                                    connection,
                                                    operationDataHandler);

    final SchemaCrawler crawler = new SchemaCrawler(dataSource, crawlHandler);
    crawler.crawl(schemaCrawlerOptions);
  }

}
