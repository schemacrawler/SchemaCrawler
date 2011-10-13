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

package schemacrawler.tools.text.operation;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.util.TextFormattingHelper.DocumentHeaderType;

/**
 * Text formatting of data.
 * 
 * @author Sualeh Fatehi
 */
final class DataTextFormatter
  extends BaseTabularFormatter<OperationOptions>
  implements DataFormatter
{

  private int dataBlockCount;
  private final Operation operation;

  /**
   * Text formatting of data.
   * 
   * @param operation
   *        Options for text formatting of data
   * @param options
   *        Options for text formatting of data
   * @param outputOptions
   *        Options for text formatting of data
   */
  DataTextFormatter(final Operation operation,
                    final OperationOptions options,
                    final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options, /* printVerboseDatabaseInfo */false, outputOptions);
    this.operation = operation;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SchemaCrawlerException
   * @see schemacrawler.tools.text.operation.DataFormatter#end()
   */
  public void end()
    throws SchemaCrawlerException
  {
    if (operation == Operation.count)
    {
      out.println(formattingHelper.createObjectEnd());
    }

    super.end();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.text.operation.DataFormatter#handleData(java.lang.String,
   *      java.sql.ResultSet)
   */
  public void handleData(final String title, final ResultSet rows)
    throws SchemaCrawlerException
  {
    if (dataBlockCount == 0)
    {
      printHeader();
    }

    if (operation == Operation.count)
    {
      handleAggregateOperationForTable(title, rows);
    }
    else
    {
      out.println(formattingHelper.createObjectStart(title));
      try
      {
        final DataResultSet dataRows = new DataResultSet(rows,
                                                         options.isShowLobs());

        out
          .println(formattingHelper.createRowHeader(dataRows.getColumnNames()));

        if (options.isMergeRows() && dataRows.width() > 1)
        {
          iterateRowsAndMerge(dataRows);
        }
        else
        {
          iterateRows(dataRows);
        }
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerException(e.getMessage(), e);
      }
      out.println(formattingHelper.createObjectEnd());
    }

    dataBlockCount++;
  }

  private void doHandleOneRow(final List<String> row,
                              final String lastColumnData)
  {
    if (row.isEmpty())
    {
      return;
    }
    final List<String> outputRow = new ArrayList<String>();
    // output
    outputRow.addAll(row);
    outputRow.add(lastColumnData);
    final String[] columnData = outputRow.toArray(new String[outputRow.size()]);
    out.println(formattingHelper.createRow(columnData));
  }

  private String getMessage(final double aggregate)
  {
    final Number number;
    if (Math.abs(aggregate - (int) aggregate) < 1E-10D)
    {
      number = Integer.valueOf((int) aggregate);
    }
    else
    {
      number = Double.valueOf(aggregate);
    }
    final String message = operation.getCountMessage(number);
    return message;
  }

  /**
   * Handles an aggregate operation, such as a count, for a given table.
   * 
   * @param title
   *        Title
   * @param results
   *        Results
   */
  private void handleAggregateOperationForTable(final String title,
                                                final ResultSet results)
    throws SchemaCrawlerException
  {
    long aggregate = 0;
    try
    {
      if (results.next())
      {
        aggregate = results.getLong(1);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Could not obtain aggregate data", e);
    }
    final String message = getMessage(aggregate);
    //
    out.println(formattingHelper.createNameRow(title, message, false));
  }

  private void iterateRows(final DataResultSet dataRows)
    throws SQLException
  {
    while (dataRows.next())
    {
      final List<String> currentRow = dataRows.row();
      final String[] columnData = currentRow.toArray(new String[currentRow
        .size()]);
      out.println(formattingHelper.createRow(columnData));
    }
  }

  private void iterateRowsAndMerge(final DataResultSet dataRows)
    throws SQLException
  {
    List<String> previousRow = new ArrayList<String>();
    List<String> currentRow;
    StringBuilder currentRowLastColumn = new StringBuilder();
    while (dataRows.next())
    {
      currentRow = dataRows.row();
      final String lastColumnDataString = currentRow
        .remove(currentRow.size() - 1);

      if (currentRow.equals(previousRow))
      {
        currentRowLastColumn.append(lastColumnDataString);
      }
      else
      {
        // At this point, we have a new row coming in, so dump the
        // previous merged row out
        doHandleOneRow(previousRow, currentRowLastColumn.toString());
        // reset
        currentRowLastColumn = new StringBuilder();
        // save the last column
        currentRowLastColumn.append(lastColumnDataString);
      }

      previousRow = currentRow;
    }
    // Dump the last row out
    doHandleOneRow(previousRow, currentRowLastColumn.toString());
  }

  private void printHeader()
  {
    if (operation != null)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                operation.getDescription()));
    }
    else
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                "Query"));
    }

    if (operation == Operation.count)
    {
      out
        .println(formattingHelper.createObjectStart(operation.getDescription()));
    }
  }

}
