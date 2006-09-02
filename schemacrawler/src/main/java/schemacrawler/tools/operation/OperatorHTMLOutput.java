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
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.tools.util.FormatUtils;

/**
 * 
 */
final class OperatorHTMLOutput
  extends BaseOperator
{

  private static final String FIELD_BEGIN = "<td>";
  private static final String FIELD_END = "</td>";
  private static final String FIELD_SEPARATOR = "</td><td>";

  /**
   * Constructs a new table dropper.
   * 
   * @param operation
   *          Operation to perform.
   * @param connection
   *          Database connection to use
   */
  OperatorHTMLOutput(final OperatorOptions options, final String query,
                     final Connection connection, final DataHandler dataHandler)
    throws SchemaCrawlerException
  {
    super(options, query, connection, dataHandler);
  }

  /**
   * @see BaseOperator#begin()
   */
  public void begin()
    throws SchemaCrawlerException
  {
    super.begin();
    if (!getNoHeader())
    {
      out.println(FormatUtils.HTML_HEADER);
      out.flush();
    }
    out.print("<table>");
    out.println("  <caption>" + getOperation().getOperationDescription()
                + "</caption>");
  }

  /**
   * @see BaseOperator#end()
   */
  public void end()
    throws SchemaCrawlerException
  {

    out.print("</table>");
    out.print("<p>&nbsp;</p>");
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

    out.print("<tr>");
    out.print(FIELD_BEGIN);
    out.print(tableName);
    out.print(FIELD_SEPARATOR);
    out.print("<div style=\"text-align: right\">");
    out.print(message);
    out.print("</div>");
    out.print(FIELD_END);
    out.print("</tr>");

    out.println();
  }

}
