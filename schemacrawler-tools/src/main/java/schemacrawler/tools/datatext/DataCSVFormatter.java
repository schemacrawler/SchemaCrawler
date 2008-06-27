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

package schemacrawler.tools.datatext;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.util.FormatUtils;

/**
 * Formats data as CSV for output.
 * 
 * @author Sualeh Fatehi
 */
final class DataCSVFormatter
  extends BaseDataTextFormatter
{

  /**
   * @see BaseDataTextFormatter#BaseDataTextFormatter(DataTextFormatOptions)
   */
  DataCSVFormatter(final DataTextFormatOptions options)
    throws SchemaCrawlerException
  {
    super(options);
  }

  /**
   * Handles a row output.
   * 
   * @param columnNames
   *        Names of the columns.
   * @param columnData
   *        Column data.
   */
  @Override
  public void handleRow(
  final String[] columnNames, final String[] columnData)
  {
    printRowCsv(columnData);
    out.flush();
  }

  /**
   *
   */
  @Override
  public void handleRowsBegin()
  {

  }

  /**
   *
   */
  @Override
  public void handleRowsEnd()
  {
    out.println();
  }

  /**
   * @param columnNames
   *        Names of the columns.
   */
  @Override
  public void handleRowsHeader(final String[] columnNames)
  {
    printRowCsv(columnNames);
  }

  /**
   * Handles metadata information.
   * 
   * @param title
   *        Execution title.
   */
  public void handleTitle(final String title)
  {
    out.println();
    out.println(title);
    out.println(FormatUtils.repeat("-", FormatUtils.MAX_LINE_LENGTH));
    out.flush();
  }

  /**
   * Prints a row of data as CSV.
   * 
   * @param fields
   *        Fields in the row.
   */
  private void printRowCsv(final String[] fields)
  {
    final String fieldSeparator = ",";

    for (int i = 0; i < fields.length; i++)
    {
      if (i > 0)
      {
        out.print(fieldSeparator);
      }
      out.print(FormatUtils.escapeAndQuoteForExcelCsv(fields[i]));
    }
    out.println();
  }

}
