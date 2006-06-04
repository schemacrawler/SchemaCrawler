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

package schemacrawler.tools.datatext;


import schemacrawler.tools.util.FormatUtils;
import sf.util.Entities;

/**
 * Formats data as HTML for output.
 * 
 * @author sfatehi
 */
final class DataHTMLFormatter
  extends BaseDataTextFormatter
{

  /**
   * @see BaseDataTextFormatter#BaseDataTextFormatter(DataTextFormatOptions,
   *      Writer)
   */
  DataHTMLFormatter(final DataTextFormatOptions options)
  {
    super(options);
  }

  /**
   * Handles the begin of the execution.
   */
  public void begin()
  {
    if (!getNoHeader())
    {
      out.println(FormatUtils.HTML_HEADER);
      out.flush();
    }
  }

  /**
   * Handles metadata information.
   * 
   * @param databaseInfo
   *          Database info.
   */
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
   * Handles metadata information.
   * 
   * @param title
   *          Execution title.
   */
  public void handleTitle(final String title)
  {
    out.println("<table>");
    out.println("  <caption>" + title + "</caption>");
  }

  /**
   * Handles the end of the execution.
   */
  public void end()
  {
    if (!getNoFooter())
    {
      out.println(FormatUtils.HTML_FOOTER);
    }
    super.end();
  }

  /**
   *
   */
  public void handleRowsBegin()
  {

  }

  /**
   * @param columnNames
   *          Names of the columns.
   */
  public void handleRowsHeader(final String[] columnNames)
  {
    out.println(" <tr>");
    final int count = columnNames.length;
    for (int i = 0; i < count; i++)
    {
      out.println(" <th>" + columnNames[i] + "</th>");
    }
    out.println(" </tr>");
  }

  /**
   * Handles a row output.
   * 
   * @param columnNames
   *          Names of the columns.
   * @param columnData
   *          Column data.
   */
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
  public void handleRowsEnd()
  {
    out.println("  </table>");
    out.flush();
  }

}
