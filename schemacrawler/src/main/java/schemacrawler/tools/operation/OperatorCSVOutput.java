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

package schemacrawler.tools.operation;


import java.sql.Connection;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.tools.util.FormatUtils;
import sf.util.Utilities;

/**
 * 
 */
final class OperatorCSVOutput
  extends BaseOperator
{

  private static final String FIELD_SEPARATOR = ",";

  /**
   * Constructs a new table dropper.
   * 
   * @param operation
   *          Operation to perform.
   * @param connection
   *          Database connection to use
   */
  OperatorCSVOutput(final OperatorOptions options, final String query,
                    final Connection connection, final DataHandler dataHandler)
    throws SchemaCrawlerException
  {
    super(options, query, connection, dataHandler);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.operation.BaseOperator#handleTable(int,
   *      java.lang.String, java.lang.String, int, java.lang.String)
   */
  public void handleTable(final int ordinalPosition, final String tableName,
                          final String tableType, final long count,
                          final String message)
  {
    out.print(FormatUtils.escapeAndQuoteForExcelCsv(tableName));
    out.print(FIELD_SEPARATOR);
    out.print(FormatUtils.escapeAndQuoteForExcelCsv(message));
    out.println();
  }

  /**
   * @see BaseOperator#end()
   */
  public void end()
    throws SchemaCrawlerException
  {

    if (!getNoFooter())
    {
      out.println(Utilities.NEWLINE + getTableCount() + " tables");
    }
    super.end();
  }

}
