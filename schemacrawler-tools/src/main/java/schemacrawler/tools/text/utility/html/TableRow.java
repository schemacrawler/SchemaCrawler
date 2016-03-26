/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.utility.html;


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
    final StringBuilder buffer = new StringBuilder(1024);
    buffer.append("\t<tr>").append(System.lineSeparator());
    for (final TableCell cell: cells)
    {
      buffer.append("\t\t").append(cell).append(System.lineSeparator());
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
    final StringBuilder buffer = new StringBuilder(1024);

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
