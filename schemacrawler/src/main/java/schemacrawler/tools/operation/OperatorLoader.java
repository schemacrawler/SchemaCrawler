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

import schemacrawler.crawl.CrawlHandler;
import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.tools.OutputFormat;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class OperatorLoader
{

  /**
   * Instantiates a text formatter type of CrawlHandler from the
   * mnemonic string.
   * 
   * @param options
   *        Options
   * @param connection
   *        Open database connection
   * @param dataHandler
   *        Data handler
   * @return CrawlHandler instance
   * @throws schemacrawler.crawl.SchemaCrawlerException
   *         On an exception
   */
  public static CrawlHandler load(final OperatorOptions options,
                                  final Connection connection,
                                  final DataHandler dataHandler)
    throws SchemaCrawlerException
  {

    if (!canLoad(options))
    {
      return null;
    }

    final Operation operation = options.getOperation();
    CrawlHandler handler = null;
    String query;
    if (operation == Operation.QUERYOVER)
    {
      query = options.getQuery();
    }
    else
    {
      query = operation.getQuery();
    }

    final OutputFormat outputFormatType = options.getOutputOptions()
      .getOutputFormat();
    if (outputFormatType == OutputFormat.HTML)
    {
      handler = new OperatorHTMLOutput(options, query, connection, dataHandler);
    }
    else
    {
      handler = new OperatorTextOutput(options, query, connection, dataHandler);
    }

    return handler;

  }

  /**
   * Checks if the CrawlHandler mnemonic is valid.
   * 
   * @param operation
   *        Mnemonic name for a CrawlHandler
   * @return True if the mnemonic is known
   */
  private static boolean canLoad(final OperatorOptions options)
  {
    return options.getOperation() != null;
  }

  private OperatorLoader()
  {
  }

}
