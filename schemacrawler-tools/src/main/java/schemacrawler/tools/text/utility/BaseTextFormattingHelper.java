/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import static sf.util.Utility.NEWLINE;
import schemacrawler.tools.options.OutputFormat;

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
    final StringBuilder dashedSeparator = new StringBuilder(72);
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

  @Override
  public String createDefinitionRow(final String definition)
  {
    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(definition,
                          0,
                          Alignment.left,
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
  @Override
  public String createDescriptionRow(final String description)
  {
    final TableRow row = new TableRow(outputFormat);
    row.add(newTableCell("", "ordinal", outputFormat));
    row.add(new TableCell(description,
                          0,
                          Alignment.left,
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
  @Override
  public String createDetailRow(final String ordinal,
                                final String subName,
                                final String type)
  {
    final int subNameWidth = 32;
    final int typeWidth = 28;

    final TableRow row = new TableRow(outputFormat);
    if (sf.util.Utility.isBlank(ordinal))
    {
      row.add(newTableCell("", "ordinal", outputFormat));
    }
    else
    {
      row.add(new TableCell(ordinal,
                            2,
                            Alignment.left,
                            1,
                            "ordinal",
                            outputFormat));
    }
    row.add(new TableCell(subName,
                          subNameWidth,
                          Alignment.left,
                          1,
                          "subname",
                          outputFormat));
    row.add(new TableCell(type,
                          typeWidth,
                          Alignment.left,
                          1,
                          "type",
                          outputFormat));
    return row.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see TextFormattingHelper#createEmptyRow()
   */
  @Override
  public String createEmptyRow()
  {
    return new TableRow(outputFormat, 3).toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.text.utility.TextFormattingHelper#createNameRow(java.lang.String,
   *      java.lang.String, boolean)
   */
  @Override
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
                          Alignment.left,
                          2,
                          "name" + (underscore? " underscore": ""),
                          outputFormat));
    row
      .add(new TableCell(description,
                         descriptionWidth,
                         Alignment.right,
                         1,
                         "description right" + (underscore? " underscore": ""),
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
   *      java.lang.String, Alignment)
   */
  @Override
  public String createNameValueRow(final String name,
                                   final String value,
                                   final Alignment valueAlignment)
  {
    final int nameWidth = 40;
    final int valueWidth = 70 - nameWidth;

    final Alignment alignmentForValue = valueAlignment == null? Alignment.left
                                                              : valueAlignment;
    final String valueStyle = "property_value"
                              + (alignmentForValue == Alignment.left? ""
                                                                    : " right");

    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(name,
                          nameWidth,
                          Alignment.left,
                          1,
                          "property_name",
                          outputFormat));
    row.add(new TableCell(value,
                          valueWidth,
                          alignmentForValue,
                          1,
                          valueStyle,
                          outputFormat));
    return row.toString();
  }

  /**
   * Called to handle the row output.
   * 
   * @param columnData
   *        Column data
   */
  @Override
  public String createRow(final String... columnData)
  {
    OutputFormat outputFormat = this.outputFormat;
    if (outputFormat == OutputFormat.text)
    {
      outputFormat = OutputFormat.tsv;
    }
    final TableRow row = new TableRow(outputFormat);
    for (final String element: columnData)
    {
      row.add(newTableCell(element, "", outputFormat));
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
  @Override
  public String createRowHeader(final String... columnNames)
  {
    OutputFormat outputFormat = this.outputFormat;
    if (outputFormat == OutputFormat.text)
    {
      outputFormat = OutputFormat.tsv;
    }
    final TableRow row = new TableRow(outputFormat);
    for (final String columnName: columnNames)
    {
      row.add(newTableCell(columnName, "name", outputFormat));
    }
    return row.toString();
  }

  private TableCell newTableCell(final String text,
                                 final String styleClass,
                                 final OutputFormat outputFormat)
  {
    return new TableCell(text, 0, Alignment.left, 1, styleClass, outputFormat);
  }

}
