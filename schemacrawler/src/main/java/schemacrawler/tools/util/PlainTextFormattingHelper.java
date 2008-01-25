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


import sf.util.Utilities;

/**
 * Methods to format entire rows of output as text.
 * 
 * @author Sualeh Fatehi
 */
public class PlainTextFormattingHelper
  implements TextFormattingHelper
{

  private final String outputFormat;

  /**
   * Constructor.
   * 
   * @param outputFormat
   *        Output format - text or CSV.
   */
  public PlainTextFormattingHelper(final String outputFormat)
  {
    this.outputFormat = outputFormat;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createDefinitionRow(java.lang.String)
   */
  public String createDefinitionRow(final String definition)
  {
    final StringBuffer row = new StringBuffer();
    row.append(getFieldSeparator());
    row.append(definition);
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
    final int subNameWidth = 32;
    final int typeWidth = 28;

    final StringBuffer row = new StringBuffer();
    row.append(getFieldSeparator());
    if (!Utilities.isBlank(ordinal))
    {
      row.append(format(ordinal, 2, true));
      row.append(getFieldSeparator());
    }
    row.append(format(subName, subNameWidth, true));
    row.append(getFieldSeparator());
    row.append(format(type, typeWidth, true));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createEmptyRow()
   */
  public String createEmptyRow()
  {
    return "";
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createNameRow(java.lang.String,
   *      java.lang.String)
   */
  public String createNameRow(final String name, final String description)
  {
    final int nameWidth = 36;
    final int descriptionWidth = 34;

    boolean overlay = false;
    final int nameLength = name.length();
    final int descriptionLength = description.length();
    final int fieldSeparatorLength = getFieldSeparator().length();
    final int minimumLength = nameLength + descriptionLength
                              + fieldSeparatorLength;
    final int totalLength = nameWidth + descriptionWidth + fieldSeparatorLength;
    if (nameLength > nameWidth && descriptionLength < descriptionWidth)
    {
      overlay = true;
    }

    final StringBuffer row = new StringBuffer();
    if (overlay)
    {
      row.append(name);
      row.append(getFieldSeparator());
      row.append(FormatUtils.repeat(" ", totalLength - minimumLength));
      row.append(description);
    }
    else
    {
      row.append(format(name, nameWidth, true));
      row.append(getFieldSeparator());
      row.append(format(description, descriptionWidth, false));
    }
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
    final int nameWidth = 36;

    final StringBuffer row = new StringBuffer();
    row.append(format(name, nameWidth, true));
    row.append(getFieldSeparator());
    row.append(value);
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.util.TextFormattingHelper#createSeparatorRow()
   */
  public String createSeparatorRow()
  {
    return FormatUtils.repeat("-", FormatUtils.MAX_LINE_LENGTH);
  }

  private String format(final String text,
                        final int maxWidth,
                        final boolean alignLeft)
  {
    if (outputFormat.equalsIgnoreCase("csv"))
    {
      return FormatUtils.escapeAndQuoteForExcelCsv(text);
    }
    else
    {
      if (alignLeft)
      {
        return FormatUtils.padRight(text, maxWidth);
      }
      else
      {
        return FormatUtils.padLeft(text, maxWidth);
      }
    }
  }

  private String getFieldSeparator()
  {
    if (outputFormat.equalsIgnoreCase("csv"))
    {
      return ",";
    }
    else
    {
      return "  ";
    }
  }

}
