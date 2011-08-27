/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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

package schemacrawler.tools.text.util;


import static sf.util.Utility.NEWLINE;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.text.util.TableCell.Align;

/**
 * Methods to format entire rows of output as HTML.
 * 
 * @author Sualeh Fatehi
 */
abstract class BaseTextFormattingHelper
  implements TextFormattingHelper
{

  /**
   * System specific line separator character.
   */
  static final String DASHED_SEPARATOR = separator("-");

  static String separator(final String pattern)
  {
    final StringBuilder dashedSeparator = new StringBuilder();
    for (int i = 0; i < 72 / pattern.length(); i++)
    {
      dashedSeparator.append(pattern);
    }
    return dashedSeparator.toString();
  }

  private final OutputFormat outputFormat;

  BaseTextFormattingHelper(final OutputFormat outputFormat)
  {
    this.outputFormat = outputFormat;
  }

  public String createDefinitionRow(final String definition)
  {
    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(definition,
                          0,
                          Align.left,
                          3,
                          "definition",
                          outputFormat));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see TextFormattingHelper#createDescriptionRow(java.lang.String)
   */
  public String createDescriptionRow(final String description)
  {
    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell("", "ordinal", outputFormat));
    row.add(new TableCell(description,
                          0,
                          Align.left,
                          2,
                          "definition",
                          outputFormat));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see TextFormattingHelper#createDetailRow(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public String createDetailRow(final String ordinal,
                                final String subName,
                                final String type)
  {
    final int subNameWidth = 32;
    final int typeWidth = 28;

    final TableRow row = new TableRow(outputFormat);
    if (sf.util.Utility.isBlank(ordinal))
    {
      row.add(new TableCell("", "ordinal", outputFormat));
    }
    else
    {
      row
        .add(new TableCell(ordinal, 2, Align.left, 1, "ordinal", outputFormat));
    }
    row.add(new TableCell(subName,
                          subNameWidth,
                          Align.left,
                          1,
                          "subname",
                          outputFormat));
    row
      .add(new TableCell(type, typeWidth, Align.left, 1, "type", outputFormat));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see TextFormattingHelper#createEmptyRow()
   */
  public String createEmptyRow()
  {
    return new TableRow(outputFormat, 4).toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.text.util.TextFormattingHelper#createNameRow(java.lang.String,
   *      java.lang.String, boolean)
   */
  public String createNameRow(final String name,
                              final String description,
                              final boolean underscore)
  {
    int nameWidth = 34;
    int descriptionWidth = 36;
    // Adjust widths
    if (name.length() > nameWidth && description.length() < descriptionWidth)
    {
      descriptionWidth = Math.max(description.length(),
                                  descriptionWidth
                                      - (name.length() - nameWidth));
    }
    if (description.length() > descriptionWidth && name.length() < nameWidth)
    {
      nameWidth = Math.max(name.length(),
                           nameWidth
                               - (description.length() - descriptionWidth));
    }

    String nameRowString;
    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(name,
                          nameWidth,
                          Align.left,
                          2,
                          "name" + (underscore? " underscore": ""),
                          outputFormat));
    row.add(new TableCell(description,
                          descriptionWidth,
                          Align.right,
                          1,
                          "description" + (underscore? " underscore": ""),
                          outputFormat));
    nameRowString = row.toString();

    if (underscore && outputFormat != OutputFormat.html)
    {
      nameRowString = nameRowString + NEWLINE + DASHED_SEPARATOR;
    }

    return nameRowString;
  }

  /**
   * {@inheritDoc}
   * 
   * @see TextFormattingHelper#createNameValueRow(java.lang.String,
   *      java.lang.String)
   */
  public String createNameValueRow(final String name, final String value)
  {
    final int nameWidth = 36;

    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(name, nameWidth, Align.left, 1, "", outputFormat));
    row.add(new TableCell(value, "", outputFormat));
    return row.toString();
  }

  /**
   * Called to handle the row output.
   * 
   * @param columnData
   *        Column data
   */
  public String createRow(final String[] columnData)
  {
    OutputFormat outputFormat = this.outputFormat;
    if (outputFormat == OutputFormat.text)
    {
      outputFormat = OutputFormat.csv;
    }
    final TableRow row = new TableRow(outputFormat);
    for (final String element: columnData)
    {
      row.add(new TableCell(element, "", outputFormat));
    }
    return row.toString();
  }

  /**
   * Called to handle the header output. Handler to be implemented by
   * subclass.
   * 
   * @param columnNames
   *        Column names
   */
  public String createRowHeader(final String[] columnNames)
  {
    OutputFormat outputFormat = this.outputFormat;
    if (outputFormat == OutputFormat.text)
    {
      outputFormat = OutputFormat.csv;
    }
    final TableRow row = new TableRow(outputFormat);
    for (final String columnName: columnNames)
    {
      row.add(new TableCell(columnName, "name", outputFormat));
    }
    return row.toString();
  }

}
