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


import schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.util.FormatUtils;

/**
 * Formats data as plain text for output.
 * 
 * @author Sualeh Fatehi
 */
public final class DataPlainTextFormatter
  extends BaseDataTextFormatter
{

  /**
   * Formats data as plain text for output.
   * 
   * @param options
   *        Options
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public DataPlainTextFormatter(final DataTextFormatOptions options)
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
  public void handleRow(final String[] columnNames, final String[] columnData)
  {
    for (int i = 0; i < columnData.length; i++)
    {
      final String columnName = columnNames[i];
      if (columnData[i] == null)
      {
        continue;
      }
      out.println(columnName + ": " + columnData[i]);
    }
    out.println(FormatUtils.repeat("-", FormatUtils.MAX_LINE_LENGTH));
    out.flush();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseDataTextFormatter#handleRowsBegin()
   */
  @Override
  public void handleRowsBegin()
  {

  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseDataTextFormatter#handleRowsEnd()
   */
  @Override
  public void handleRowsEnd()
  {

  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseDataTextFormatter#handleRowsHeader(String[])
   */
  @Override
  public void handleRowsHeader(@SuppressWarnings("unused")
  final String[] columnNames)
  {

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
    out.println();
    out.println(FormatUtils.repeat("-", FormatUtils.MAX_LINE_LENGTH));
    out.flush();
  }

}
