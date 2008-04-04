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

package schemacrawler.tools.datatext;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.util.Entities;
import schemacrawler.tools.util.FormatUtils;

/**
 * Formats data as HTML for output.
 * 
 * @author Sualeh Fatehi
 */
final class DataHTMLFormatter
  extends BaseDataTextFormatter
{

  /**
   * @see BaseDataTextFormatter#BaseDataTextFormatter(DataTextFormatOptions)
   */
  DataHTMLFormatter(final DataTextFormatOptions options)
    throws SchemaCrawlerException
  {
    super(options);
  }

  /**
   * Handles the begin of the execution.
   */
  @Override
  public void begin()
  {
    if (!getNoHeader())
    {
      out.println(FormatUtils.HTML_HEADER);
      out.flush();
    }
  }

  /**
   * Handles the end of the execution.
   */
  @Override
  public void end()
  {
    if (!getNoFooter())
    {
      out.println(FormatUtils.HTML_FOOTER);
    }
    super.end();
  }

  /**
   * Handles metadata information.
   * 
   * @param databaseInfo
   *        Database info.
   */
  @Override
  public void handleMetadata(final String databaseInfo)
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
   * Handles a row output.
   * 
   * @param columnNames
   *        Names of the columns.
   * @param columnData
   *        Column data.
   */
  @Override
  public void handleRow(final String[] columnNames, final String[] columnData)
  {
    out.println("    <tr>");

    for (int i = 0; i < columnData.length; i++)
    {
      if (columnData[i] == null)
      {
        out.println("      <td>&nbsp;</td>");
      }
      else
      {
        out.println("      " + "<td " + "title=\"" + columnNames[i] + "\""
                    + ">" + Entities.XML.escape(columnData[i]) + "</td>");
      }
    }
    out.println("    </tr>");
  }

  /**
   *
   */
  @Override
  public void handleRowsBegin()
  {
    out.println("  <tbody>");
    out.flush();
  }

  /**
   *
   */
  @Override
  public void handleRowsEnd()
  {
    out.println("  </tbody>");
    out.println("  </table>");
    out.flush();
  }

  /**
   * @param columnNames
   *        Names of the columns.
   */
  @Override
  public void handleRowsHeader(final String[] columnNames)
  {
    out.println(" <thead>");
    out.println(" <tr>");
    final int count = columnNames.length;
    for (int i = 0; i < count; i++)
    {
      out.println(" <th>" + columnNames[i] + "</th>");
    }
    out.println(" </tr>");
    out.println(" </thead>");
  }

  /**
   * Handles metadata information.
   * 
   * @param title
   *        Execution title.
   */
  public void handleTitle(final String title)
  {
    out.println("<p></p>");
    out.println("<table>");
    out.println("  <caption>" + title + "</caption>");
  }

}
