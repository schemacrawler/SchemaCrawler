/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.tools.text.utility;


import schemacrawler.tools.options.OutputFormat;

/**
 * Represents an HTML table row.
 * 
 * @author Sualeh Fatehi
 */
final class TableCell
{

  enum Align
  {

    left, right;
  }

  /**
   * Enclose the value in quotes and escape the quote and comma
   * characters that are inside.
   * 
   * @param text
   *        Text that needs to be escaped and quoted
   * @return Text, escaped and quoted.
   */
  private static String escapeAndQuoteCsv(final String text)
  {
    final char QUOTE = '\"';
    final char SEPARATOR = ',';

    final String value = String.valueOf(text);
    final int length = value.length();
    if (length == 0)
    {
      return "\"\"";
    }

    if (value.indexOf(SEPARATOR) < 0 && value.indexOf(QUOTE) < 0)
    {
      return value;
    }

    final StringBuilder sb = new StringBuilder(length);
    sb.append(QUOTE);
    for (int i = 0; i < length; i++)
    {
      final char c = value.charAt(i);
      if (c == QUOTE)
      {
        sb.append(QUOTE).append(c);
      }
      else
      {
        sb.append(c);
      }
    }
    sb.append(QUOTE);

    return sb.toString();
  }

  private final OutputFormat outputFormat;
  private final String styleClass;
  private final int colSpan;
  private final int characterWidth;
  private final Align align;

  private final String text;

  TableCell(final String text,
            final int characterWidth,
            final Align align,
            final int colSpan,
            final String styleClass,
            final OutputFormat outputFormat)
  {
    this.outputFormat = outputFormat;
    this.colSpan = colSpan;
    this.styleClass = styleClass;
    this.text = text;
    this.characterWidth = characterWidth;
    this.align = align;
  }

  /**
   * Converts the table cell to HTML.
   * 
   * @return HTML
   */
  @Override
  public String toString()
  {
    if (outputFormat == OutputFormat.html)
    {
      return toHtmlString();
    }
    else
    {
      return toPlainTextString();
    }
  }

  /**
   * Converts the table cell to HTML.
   * 
   * @return HTML
   */
  private String toHtmlString()
  {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("<td");
    if (colSpan > 1)
    {
      buffer.append(" colspan='").append(colSpan).append("'");
    }
    if (!sf.util.Utility.isBlank(styleClass))
    {
      buffer.append(" class='").append(styleClass).append("'");
    }
    buffer.append(">");
    buffer.append(Entities.XML.escape(String.valueOf(text)));
    buffer.append("</td>");

    return buffer.toString();
  }

  /**
   * Converts the table cell to CSV.
   * 
   * @return CSV
   */
  private String toPlainTextString()
  {
    if (outputFormat == OutputFormat.csv)
    {
      return escapeAndQuoteCsv(text);
    }
    else if (outputFormat == OutputFormat.tsv)
    {
      return String.valueOf(text);
    }
    else
    {
      if (characterWidth > 0)
      {
        if (align == Align.right)
        {
          return String.format("%" + characterWidth + "s", text);
        }
        else
        {
          return String.format("%-" + characterWidth + "s", text);
        }
      }
      else
      {
        return text;
      }
    }
  }

}
