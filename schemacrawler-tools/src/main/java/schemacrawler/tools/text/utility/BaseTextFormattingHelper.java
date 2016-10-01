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

package schemacrawler.tools.text.utility;


import static sf.util.Utility.isBlank;

import java.io.PrintWriter;

import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.utility.html.Alignment;
import schemacrawler.tools.text.utility.html.Anchor;
import schemacrawler.tools.text.utility.html.TableCell;
import schemacrawler.tools.text.utility.html.TableHeaderCell;
import schemacrawler.tools.text.utility.html.TableRow;
import sf.util.Color;

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

  protected final PrintWriter out;

  private final TextOutputFormat outputFormat;

  public BaseTextFormattingHelper(final PrintWriter out,
                                  final TextOutputFormat outputFormat)
  {
    this.out = out;
    this.outputFormat = outputFormat;
  }

  @Override
  public TextFormattingHelper append(final String text)
  {
    out.write(text);
    out.flush();

    return this;
  }

  @Override
  public String createAnchor(final String text, final String link)
  {
    return new Anchor(text,
                      false,
                      0,
                      Alignment.inherit,
                      false,
                      "",
                      Color.white,
                      link,
                      outputFormat).toString();
  }

  @Override
  public void println()
  {
    out.println();
  }

  /**
   * {@inheritDoc}
   *
   * @see TextFormattingHelper#writeDescriptionRow(java.lang.String)
   */
  @Override
  public void writeDescriptionRow(final String description)
  {
    final TableRow row = new TableRow(outputFormat);
    row.add(newTableCell("", "spacer", outputFormat));
    row.add(new TableCell(description,
                          true,
                          0,
                          Alignment.inherit,
                          false,
                          "",
                          Color.white,
                          2,
                          outputFormat));
    out.println(row.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.text.utility.TextFormattingHelper#writeDetailRow(java.lang.String,
   *      java.lang.String, java.lang.String, boolean, boolean, String)
   */
  @Override
  public void writeDetailRow(final String text1,
                             final String text2,
                             final String text3,
                             final boolean escapeText,
                             final boolean emphasize,
                             final String style)
  {
    final int text2Width = 32;
    final int text3Width = 28;
    final String text3Sytle;
    if (!isBlank(style))
    {
      text3Sytle = " " + style;
    }
    else
    {
      text3Sytle = "";
    }

    final TableRow row = new TableRow(outputFormat);
    if (isBlank(text1))
    {
      row.add(newTableCell("", "spacer", outputFormat));
    }
    else
    {
      row.add(new TableCell(text1,
                            true,
                            2,
                            Alignment.inherit,
                            false,
                            "spacer",
                            Color.white,
                            1,
                            outputFormat));
    }
    row.add(new TableCell(text2,
                          escapeText,
                          text2Width,
                          Alignment.inherit,
                          emphasize,
                          "minwidth",
                          Color.white,
                          1,
                          outputFormat));
    row.add(new TableCell(text3,
                          true,
                          text3Width,
                          Alignment.inherit,
                          false,
                          "minwidth" + text3Sytle,
                          Color.white,
                          1,
                          outputFormat));
    out.println(row.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see TextFormattingHelper#writeDetailRow(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  @Override
  public void writeDetailRow(final String text1,
                             final String text2,
                             final String text3)
  {
    writeDetailRow(text1, text2, text3, true, false, "");
  }

  /**
   * {@inheritDoc}
   *
   * @see TextFormattingHelper#writeEmptyRow()
   */
  @Override
  public void writeEmptyRow()
  {
    final TableRow tableRow = new TableRow(outputFormat);
    tableRow.add(new TableCell("",
                               true,
                               0,
                               Alignment.inherit,
                               false,
                               "",
                               Color.white,
                               3,
                               outputFormat));
    out.println(tableRow.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.text.utility.TextFormattingHelper#writeNameRow(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public void writeNameRow(final String name, final String description)
  {
    int nameWidth = 34;
    int descriptionWidth = 36;
    // Adjust widths
    if (name.length() > nameWidth && description.length() < descriptionWidth)
    {
      descriptionWidth = Math
        .max(description.length(),
             descriptionWidth - (name.length() - nameWidth));
    }
    if (description.length() > descriptionWidth && name.length() < nameWidth)
    {
      nameWidth = Math
        .max(name.length(),
             nameWidth - (description.length() - descriptionWidth));
    }

    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(name,
                          true,
                          nameWidth,
                          Alignment.inherit,
                          false,
                          "name",
                          Color.white,
                          2,
                          outputFormat));
    row.add(new TableCell(description,
                          true,
                          descriptionWidth,
                          Alignment.right,
                          false,
                          "description right",
                          Color.white,
                          1,
                          outputFormat));

    out.println(row.toString());
  }

  /**
   * {@inheritDoc}
   *
   * @see TextFormattingHelper#writeNameValueRow(java.lang.String,
   *      java.lang.String, Alignment)
   */
  @Override
  public void writeNameValueRow(final String name,
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
    row
      .add(new TableCell(name,
                         true,
                         nameWidth,
                         Alignment.inherit,
                         false,
                         "property_name",
                         Color.white,
                         1,
                         outputFormat));
    row.add(new TableCell(value,
                          true,
                          valueWidth,
                          alignmentForValue,
                          false,
                          valueStyle,
                          Color.white,
                          1,
                          outputFormat));

    out.println(row.toString());
  }

  /**
   * Called to handle the row output.
   *
   * @param columnData
   *        Column data
   */
  @Override
  public void writeRow(final Object... columnData)
  {
    TextOutputFormat outputFormat = this.outputFormat;
    if (outputFormat == TextOutputFormat.text)
    {
      outputFormat = TextOutputFormat.tsv;
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

    out.println(row.toString());
  }

  /**
   * Called to handle the header output. Handler to be implemented by
   * subclass.
   *
   * @param columnNames
   *        Column names
   */
  @Override
  public void writeRowHeader(final String... columnNames)
  {
    TextOutputFormat outputFormat = this.outputFormat;
    if (outputFormat == TextOutputFormat.text)
    {
      outputFormat = TextOutputFormat.tsv;
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

    out.println(row.toString());
  }

  @Override
  public void writeWideRow(final String definition, final String style)
  {
    final TableRow row = new TableRow(outputFormat);
    row.add(new TableCell(definition,
                          true,
                          0,
                          Alignment.inherit,
                          false,
                          style,
                          Color.white,
                          3,
                          outputFormat));
    out.println(row.toString());
  }

  private TableCell newTableCell(final String text,
                                 final String styleClass,
                                 final TextOutputFormat outputFormat)
  {
    return new TableCell(text,
                         true,
                         0,
                         Alignment.inherit,
                         false,
                         styleClass,
                         Color.white,
                         1,
                         outputFormat);
  }

}
