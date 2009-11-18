/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.io.BufferedInputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.BaseFormatter;
import schemacrawler.tools.operation.Operation;
import schemacrawler.tools.operation.OperationOptions;
import schemacrawler.tools.util.TextFormattingHelper.DocumentHeaderType;

/**
 * Text formatting of data.
 * 
 * @author Sualeh Fatehi
 */
public final class DataTextFormatter
  extends BaseFormatter<OperationOptions>
{

  private static final Logger LOGGER = Logger.getLogger(DataTextFormatter.class
    .getName());

  private static final String BINARY = "<binary>";

  private final Operation operation;

  /**
   * Text formatting of data.
   * 
   * @param options
   *        Options for text formatting of data
   */
  public DataTextFormatter(final OperationOptions options)
    throws SchemaCrawlerException
  {
    super(options);
    operation = ((OperationOptions) options).getOperation();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.execute.DataHandler#begin()
   */
  public void begin()
  {
    if (!options.getOutputOptions().isNoHeader())
    {
      out.println(formattingHelper.createDocumentStart());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.execute.DataHandler#end()
   */
  public void end()
  {
    if (operation == Operation.count)
    {
      out.println(formattingHelper.createObjectEnd());
    }

    if (!options.getOutputOptions().isNoFooter())
    {
      out.println(formattingHelper.createDocumentEnd());
    }
    out.flush();
    //
    options.getOutputOptions().closeOutputWriter(out);
  }

  @Override
  public void handle(final DatabaseInfo databaseInfo)
  {
    super.handle(databaseInfo);

    // If this is an operation, print the operation headers,
    // in preparation for data handling
    if (operation != null)
    {
      out.println(formattingHelper.createHeader(DocumentHeaderType.subTitle,
                                                operation.getDescription()));
      if (operation == Operation.count)
      {
        out.println(formattingHelper.createObjectStart(operation
          .getDescription()));
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.execute.DataHandler#handleData(java.sql.ResultSet)
   */
  public void handleData(final String title, final ResultSet rows)
    throws SchemaCrawlerException
  {
    if (operation == Operation.count)
    {
      handleAggregateOperationForTable(title, rows);
    }
    else
    {
      out.println(formattingHelper.createObjectStart(title));
      try
      {
        final ResultSetMetaData rsm = rows.getMetaData();
        final int columnCount = rsm.getColumnCount();
        final String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++)
        {
          columnNames[i] = rsm.getColumnName(i + 1);
        }
        out.println(formattingHelper.createRowHeader(columnNames));

        if (options.isMergeRows() && columnCount > 1)
        {
          iterateRowsAndMerge(rows, columnNames);
        }
        else
        {
          iterateRows(rows, columnNames.length);
        }
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerException(e.getMessage(), e);
      }
      out.println(formattingHelper.createObjectEnd());
    }
  }

  private String convertColumnDataToString(final Object columnData)
  {
    String columnDataString;
    if (columnData == null)
    {
      columnDataString = "<null>";
    }
    else if (columnData instanceof Clob || columnData instanceof Blob)
    {
      columnDataString = BINARY;
      if (options.isShowLobs())
      {
        columnDataString = readLob(columnData);
      }
    }
    else
    {
      columnDataString = columnData.toString();
    }
    return columnDataString;
  }

  private void doHandleOneRow(final List<String> row,
                              final String lastColumnData)
    throws SchemaCrawlerException
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

    Number number;
    if (Math.abs(aggregate - (int) aggregate) < 1E-10D)
    {
      number = Integer.valueOf((int) aggregate);
    }
    else
    {
      number = Double.valueOf(aggregate);
    }
    final String message = MessageFormat.format(operation
      .getCountMessageFormat(), new Object[] {
      number
    });
    return message;
  }

  /**
   * Handles an aggregate operation, such as a count, for a given table.
   * 
   * @param table
   *        Table
   * @param results
   *        Results
   * @throws SQLException
   *         On an exception
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

  private void iterateRows(final ResultSet rows, final int columnCount)
    throws SQLException, SchemaCrawlerException
  {
    List<String> currentRow;
    while (rows.next())
    {
      currentRow = new ArrayList<String>(columnCount);
      for (int i = 0; i < columnCount; i++)
      {
        final int columnIndex = i + 1;
        final Object columnData = rows.getObject(columnIndex);
        final String columnDataString = convertColumnDataToString(columnData);
        currentRow.add(columnDataString);
      }
      final String[] columnData = currentRow.toArray(new String[currentRow
        .size()]);
      out.println(formattingHelper.createRow(columnData));
    }
  }

  private void iterateRowsAndMerge(final ResultSet rows,
                                   final String[] columnNames)
    throws SQLException, SchemaCrawlerException
  {
    final int columnCount = columnNames.length;
    List<String> previousRow = new ArrayList<String>();
    List<String> currentRow;
    StringBuilder currentRowLastColumn = new StringBuilder();
    // write out the data
    while (rows.next())
    {
      currentRow = new ArrayList<String>(columnCount - 1);
      for (int i = 0; i < columnCount - 1; i++)
      {
        final Object columnData = rows.getObject(i + 1);
        final String columnDataString = convertColumnDataToString(columnData);
        currentRow.add(columnDataString);
      }
      final Object lastColumnData = rows.getObject(columnCount);
      final String lastColumnDataString = convertColumnDataToString(lastColumnData);
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

  /**
   * Reads data from a LOB into a string. Default system encoding is
   * assumed.
   * 
   * @param columnData
   *        Column data object returned by JDBC
   * @return A string with the contents of the LOB
   */
  private String readLob(final Object columnData)
  {
    BufferedInputStream in = null;
    final String lobData;
    try
    {
      if (columnData instanceof Blob)
      {
        final Blob blob = (Blob) columnData;
        in = new BufferedInputStream(blob.getBinaryStream());
      }
      else if (columnData instanceof Clob)
      {
        final Clob clob = (Clob) columnData;
        in = new BufferedInputStream(clob.getAsciiStream());
      }
      lobData = schemacrawler.utility.Utility.readFully(in);
      return lobData;
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.FINE, "Could not read binary data", e);
      return BINARY;
    }

  }

}
