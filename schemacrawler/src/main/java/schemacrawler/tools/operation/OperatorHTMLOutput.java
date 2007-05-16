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

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.util.FormatUtils;
import schemacrawler.tools.util.HtmlFormattingHelper;

/**
 * 
 */
final class OperatorHTMLOutput
  extends BaseOperator
{

  /**
   * Constructs a new table dropper.
   * 
   * @param operation
   *        Operation to perform.
   * @param connection
   *        Database connection to use
   */
  OperatorHTMLOutput(final OperatorOptions options,
                     final String query,
                     final Connection connection,
                     final DataHandler dataHandler)
    throws SchemaCrawlerException
  {
    super(options, query, connection, dataHandler, new HtmlFormattingHelper());
  }

  /**
   * @see BaseOperator#begin()
   */
  @Override
  public void begin()
    throws SchemaCrawlerException
  {
    super.begin();
    if (!getNoHeader())
    {
      out.println(FormatUtils.HTML_HEADER);
    }
  }

  /**
   * @see BaseOperator#end()
   */
  @Override
  public void end()
    throws SchemaCrawlerException
  {
    handleEndTables();
    if (!getNoFooter())
    {
      out.println("<pre id='tableCount'>" + getTableCount() + " tables"
                  + "</pre>");
      out.println(FormatUtils.HTML_FOOTER);
    }
    super.end();
  }

  /**
   * @see BaseOperator#handle(DatabaseInfo)
   */
  @Override
  public void handle(final DatabaseInfo databaseInfo)
  {
    if (!getNoInfo())
    {
      out.println("<pre id='databaseInfo'>");
      out.println(databaseInfo);
      out.println("</pre>");
      out.flush();
    }
  }

  @Override
  protected void handleStartTables()
  {
    if (operation == Operation.count)
    {
      out.print("<table>");
      out.println("  <caption>" + getOperation().getDescription()
                  + "</caption>");
    }
  }

  private void handleEndTables()
  {
    if (operation == Operation.count)
    {
      out.print("</table>");
      out.println("<p>&nbsp;</p>");
    }
  }

}
