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

package schemacrawler.tools.util;


public final class HtmlFormattingHelper
  implements TextFormattingHelper
{

  public String createDefinitionRow(final String definition)
  {
    HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("ordinal", ""));
    row.addCell(new HtmlTableCell(3, "definition", definition));
    return row.toString();
  }

  public String createDetailRow(String ordinal, final String subName,
                                final String type, final String remarks)
  {
    HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("ordinal", ordinal));
    row.addCell(new HtmlTableCell("subname", subName));
    row.addCell(new HtmlTableCell("type", type));
    row.addCell(new HtmlTableCell("remarks", remarks));
    return row.toString();
  }

  public String createEmptyRow()
  {
    return new HtmlTableRow(4).toString();
  }

  public String createNameRow(final String name, final String description)
  {
    HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell(2, "name", name));
    row.addCell(new HtmlTableCell(2, "description", description));
    return row.toString();
  }

  public String createNameValueRow(final String name, final String value)
  {
    HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell("", name));
    row.addCell(new HtmlTableCell("", value));
    return row.toString();
  }

  public String createSeparatorRow()
  {
    HtmlTableRow row = new HtmlTableRow();
    row.addCell(new HtmlTableCell(4, "", "<hr/>"));
    return row.toString();
  }

}
