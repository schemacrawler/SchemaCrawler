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
package schemacrawler.tools.util;


import sf.util.Utilities;

/**
 * Represents an HTML table row.
 * 
 * @author Sualeh Fatehi
 */
final class HtmlTableCell
{

  private String styleClass;
  private int colSpan = 1;
  private String innerHtml;

  HtmlTableCell()
  {
    this(0, null, null);
  }

  HtmlTableCell(final String styleClass, final String innerHtml)
  {
    this.styleClass = styleClass;
    this.innerHtml = innerHtml;
  }

  HtmlTableCell(final int colSpan,
                final String styleClass,
                final String innerHtml)
  {
    this.colSpan = colSpan;
    this.styleClass = styleClass;
    this.innerHtml = innerHtml;
  }

  /**
   * Converts the table cell to HTML.
   * 
   * @return HTML
   */
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("<td");
    if (colSpan > 1)
    {
      buffer.append(" colspan='").append(colSpan).append("'");
    }
    if (!Utilities.isBlank(styleClass))
    {
      buffer.append(" class='").append(styleClass).append("'");
    }
    buffer.append(">");
    if (!Utilities.isBlank(innerHtml))
    {
      buffer.append(innerHtml);
    }
    else
    {
      buffer.append("&nbsp;");
    }
    buffer.append("</td>");

    return buffer.toString();
  }

}
