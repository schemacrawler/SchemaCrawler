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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an HTML table row.
 * 
 * @author Sualeh Fatehi
 */
final class HtmlTableRow
{

  private final List<HtmlTableCell> cells;

  HtmlTableRow()
  {
    cells = new ArrayList<HtmlTableCell>();
  }

  HtmlTableRow(final int colSpan)
  {
    cells = Arrays.asList(new HtmlTableCell[] {
      new HtmlTableCell(colSpan, null, null)
    });
  }

  /**
   * Converts the table row to HTML.
   * 
   * @return HTML
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("\t<tr>\n");
    for (final HtmlTableCell cell: cells)
    {
      buffer.append("\t\t").append(cell).append("\n");
    }
    buffer.append("\t</tr>");

    return buffer.toString();
  }

  void addCell(final HtmlTableCell cell)
  {
    cells.add(cell);
  }

}
