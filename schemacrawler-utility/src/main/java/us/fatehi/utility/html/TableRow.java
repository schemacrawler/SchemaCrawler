/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package us.fatehi.utility.html;


import static us.fatehi.utility.html.TagOutputFormat.html;
import static us.fatehi.utility.html.TagOutputFormat.text;
import static us.fatehi.utility.html.TagOutputFormat.tsv;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HTML table row.
 *
 * @author Sualeh Fatehi
 */
public final class TableRow
  implements Tag
{

  private final List<TableCell> cells;

  public TableRow()
  {
    cells = new ArrayList<>();
  }

  public TableRow add(final TableCell cell)
  {
    cells.add(cell);
    return this;
  }

  public TableCell firstCell()
  {
    if (cells.isEmpty())
    {
      return null;
    }
    return cells.get(0);
  }

  public TableCell lastCell()
  {
    if (cells.isEmpty())
    {
      return null;
    }
    return cells.get(cells.size() - 1);
  }

  @Override
  public String toString()
  {
    return getTag();
  }

  @Override
  public String getTag()
  {
    return "tr";
  }

  /**
   * Converts the table cell to HTML.
   *
   * @return HTML
   */
  public String render(final TagOutputFormat tagOutputFormat)
  {
    switch (tagOutputFormat)
    {
      case text:
        return toPlainTextString();
      case tsv:
        return toTsvString();
      case html:
      default:
        return toHtmlString();
    }
  }

  /**
   * Converts the table row to HTML.
   *
   * @return HTML
   */
  private String toHtmlString()
  {
    final StringBuilder buffer = new StringBuilder(1024);
    buffer
      .append("\t<")
      .append(getTag())
      .append(">")
      .append(System.lineSeparator());
    for (final TableCell cell : cells)
    {
      buffer
        .append("\t\t")
        .append(cell.render(html))
        .append(System.lineSeparator());
    }
    buffer
      .append("\t</")
      .append(getTag())
      .append(">");

    return buffer.toString();
  }

  private String toPlainTextString()
  {
    return toPlainTextString(text, "  ");
  }

  private String toTsvString()
  {
    return toPlainTextString(tsv, "\t");
  }

  /**
   * Converts the table row to text.
   *
   * @return Text
   */
  private String toPlainTextString(final TagOutputFormat tagOutputFormat,
                                   final String fieldSeparator)
  {
    final StringBuilder buffer = new StringBuilder(1024);

    for (int i = 0; i < cells.size(); i++)
    {
      final TableCell cell = cells.get(i);
      if (i > 0)
      {
        buffer.append(fieldSeparator);
      }
      buffer.append(cell.render(tagOutputFormat));
    }

    return buffer.toString();
  }

}
