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

package schemacrawler.tools.util;


/**
 * Methods to format entire rows of output as HTML.
 * 
 * @author Sualeh Fatehi
 */
public final class HtmlFormattingHelper
  implements TextFormattingHelper
{

  /**
   * Table end HTML.
   * 
   * @return Table end HTML
   */
  public static String createTableEnd()
  {
    return "</table>\n" + "<p></p>\n";
  }

  /**
   * Table start HTML.
   * 
   * @return Table start HTML
   */
  public static String createTableStart()
  {
    return "<table>\n";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDefinitionRow(java.lang.String)
   */
  public String createDefinitionRow(final String definition)
  {
    final HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("ordinal", ""));
    row.addCell(new HtmlTableCell(2, "definition", definition));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDetailRow(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public String createDetailRow(final String ordinal,
                                final String subName,
                                final String type)
  {
    final HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("ordinal", ordinal));
    row.addCell(new HtmlTableCell("subname", subName));
    row.addCell(new HtmlTableCell("type", type));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createEmptyRow()
   */
  public String createEmptyRow()
  {
    return new HtmlTableRow(4).toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createNameRow(java.lang.String,
   *      java.lang.String)
   */
  public String createNameRow(final String name, final String description)
  {
    final HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell(2, "name", name));
    row.addCell(new HtmlTableCell("description", description));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createNameValueRow(java.lang.String,
   *      java.lang.String)
   */
  public String createNameValueRow(final String name, final String value)
  {
    final HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("", name));
    row.addCell(new HtmlTableCell("", value));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createSeparatorRow()
   */
  public String createSeparatorRow()
  {
    final HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell(3, "", "<hr/>"));
    return row.toString();
  }

}
