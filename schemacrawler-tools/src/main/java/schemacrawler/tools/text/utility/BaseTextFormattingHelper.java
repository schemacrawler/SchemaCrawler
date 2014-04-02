/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import java.awt.Color;

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
                          Alignment.inherit,
                          false,
                          "definition",
                          Color.white,
                          3,
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
                          Alignment.inherit,
                          false,
                          "definition",
                          Color.white,
                          2,
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
                            Alignment.inherit,
                            false,
                            "ordinal",
                            Color.white,
                            1,
                            outputFormat));
    }
    row.add(new TableCell(subName,
                          subNameWidth,
                          Alignment.inherit,
                          false,
                          "subname",
                          Color.white,
                          1,
                          outputFormat));
    row.add(new TableCell(type,
                          typeWidth,
                          Alignment.inherit,
                          false,
                          "type",
                          Color.white,
                          1,
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
    final TableRow tableRow = new TableRow(outputFormat);
    tableRow.add(new TableCell("",
                               0,
                               Alignment.inherit,
                               false,
                               "",
                               Color.white,
                               3,
                               outputFormat));
    return tableRow.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.text.utility.TextFormattingHelper#createNameRow(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public String createNameRow(final String name, final String description)
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
                          Alignment.inherit,
                          false,
                          "name",
                          Color.white,
                          2,
                          outputFormat));
    row.add(new TableCell(description,
                          descriptionWidth,
                          Alignment.right,
                          false,
                          "description right",
                          Color.white,
                          1,
                          outputFormat));
    nameRowString = row.toString();

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

    final Alignment alignmentForValue = valueAlignment == null? Alignment.inherit
                                                              : valueAlignment;
    final String valueStyle = "property_value"
                              + (alignmentForValue == Alignment.inherit? ""
                                                                    : " right");

    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(name,
                          nameWidth,
                          Alignment.inherit,
                          false,
                          "property_name",
                          Color.white,
                          1,
                          outputFormat));
    row.add(new TableCell(value,
                          valueWidth,
                          alignmentForValue,
                          false,
                          valueStyle,
                          Color.white,
                          1,
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
  public String createRow(final Object... columnData)
  {
    OutputFormat outputFormat = this.outputFormat;
    if (outputFormat == OutputFormat.text)
    {
      outputFormat = OutputFormat.tsv;
    }
    final TableRow row = new TableRow(outputFormat);
    for (final Object element: columnData)
    {
      if (element == null)
      {
        row.add(newTableCell(null, "data_null", outputFormat));
      }
      else if (element instanceof BinaryData)
      {
        row.add(newTableCell(element.toString(), "data_binary", outputFormat));
      }
      else if (element instanceof Number)
      {
        row.add(newTableCell(element.toString(), "data_number", outputFormat));
      }
      else
      {
        row.add(newTableCell(element.toString(), "", outputFormat));
      }
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
      final TableHeaderCell headerCell = new TableHeaderCell(columnName,
                                                             0,
                                                             Alignment.inherit,
                                                             false,
                                                             "",
                                                             Color.white,
                                                             1,
                                                             outputFormat);
      row.add(headerCell);
    }
    return row.toString();
  }

  private TableCell newTableCell(final String text,
                                 final String styleClass,
                                 final OutputFormat outputFormat)
  {
    return new TableCell(text,
                         0,
                         Alignment.inherit,
                         false,
                         styleClass,
                         Color.white,
                         1,
                         outputFormat);
  }

}
