/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.text.utility;


import static sf.util.Utility.NEWLINE;

import java.util.ArrayList;
import java.util.List;

import schemacrawler.tools.options.TextOutputFormat;

/**
 * Represents an HTML table row.
 *
 * @author Sualeh Fatehi
 */
public final class TableRow
{

  private final TextOutputFormat outputFormat;
  private final List<TableCell> cells;

  public TableRow(final TextOutputFormat outputFormat)
  {
    this.outputFormat = outputFormat;
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

  /**
   * Converts the table row to HTML.
   *
   * @return HTML
   */
  @Override
  public String toString()
  {
    if (outputFormat == TextOutputFormat.html)
    {
      return toHtmlString();
    }
    else
    {
      return toPlainTextString();
    }
  }

  private String getFieldSeparator()
  {
    String fieldSeparator;
    switch (outputFormat)
    {
      case csv:
        fieldSeparator = ",";
        break;
      case tsv:
        fieldSeparator = "\t";
        break;
      default:
        fieldSeparator = "  ";
    }
    return fieldSeparator;
  }

  /**
   * Converts the table row to HTML.
   *
   * @return HTML
   */
  private String toHtmlString()
  {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("\t<tr>").append(NEWLINE);
    for (final TableCell cell: cells)
    {
      buffer.append("\t\t").append(cell).append(NEWLINE);
    }
    buffer.append("\t</tr>");

    return buffer.toString();
  }

  /**
   * Converts the table row to CSV.
   *
   * @return CSV
   */
  private String toPlainTextString()
  {
    final StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < cells.size(); i++)
    {
      final TableCell cell = cells.get(i);
      if (i > 0)
      {
        buffer.append(getFieldSeparator());
      }
      buffer.append(cell.toString());
    }

    return buffer.toString();
  }

}
