/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.tools.util.FormatUtils;
import sf.util.Utilities;

/**
 * Formats data as plain text for output.
 * 
 * @author sfatehi
 */
public final class DataPlainTextFormatter
  extends BaseDataTextFormatter
{

  /**
   * Formats data as plain text for output.
   * 
   * @param options
   *          Options
   */
  public DataPlainTextFormatter(final DataTextFormatOptions options)
    throws SchemaCrawlerException
  {
    super(options);
  }

  /**
   * Handles metadata information.
   * 
   * @param title
   *          Execution title.
   */
  public void handleTitle(final String title)
  {
    out.println();
    out.println(title);
    out.println();
    out.println(Utilities.repeat("-", FormatUtils.MAX_LINE_LENGTH));
    out.flush();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseDataTextFormatter#handleRowsHeader(String[])
   */
  public void handleRowsHeader(final String[] columnNames)
  {

  }

  /**
   * Handles a row output.
   * 
   * @param columnNames
   *          Names of the columns.
   * @param columnData
   *          Column data.
   */
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
    out.println(Utilities.repeat("-", FormatUtils.MAX_LINE_LENGTH));
    out.flush();
  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseDataTextFormatter#handleRowsEnd()
   */
  public void handleRowsEnd()
  {

  }

  /**
   * {@inheritDoc}
   * 
   * @see BaseDataTextFormatter#handleRowsBegin()
   */
  public void handleRowsBegin()
  {

  }

}
