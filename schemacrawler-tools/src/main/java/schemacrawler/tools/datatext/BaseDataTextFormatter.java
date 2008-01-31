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


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutorException;
import schemacrawler.tools.util.FormatUtils;
import sf.util.Utilities;

/**
 * Base functionality for the text formatting of data.
 * 
 * @author Sualeh Fatehi
 */
public abstract class BaseDataTextFormatter
  implements DataHandler
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseDataTextFormatter.class.getName());

  private static final String BINARY = "<binary>";

  protected final PrintWriter out;
  private final DataTextFormatOptions options;

  /**
   * Constructor for a base data handler that is capable of merging
   * rows.
   * 
   * @param mergeRows
   */
  BaseDataTextFormatter(final DataTextFormatOptions options)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    this.options = options;

    try
    {
      out = options.getOutputOptions().openOutputWriter();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not obtain output writer", e);
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see DataHandler#begin()
   */
  public void begin()
  {
  }

  /**
   * {@inheritDoc}
   * 
   * @see DataHandler#close()
   */
  public void close()
  {
    options.getOutputOptions().closeOutputWriter(out);
  }

  /**
   * {@inheritDoc}
   * 
   * @see DataHandler#end()
   */
  public void end()
  {
    close();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.execute.DataHandler#getPrintWriter()
   */
  public PrintWriter getPrintWriter()
  {
    return out;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.execute.DataHandler#handleData(java.sql.ResultSet)
   */
  public final void handleData(final ResultSet rows)
    throws QueryExecutorException
  {
    try
    {
      final ResultSetMetaData rsm = rows.getMetaData();
      final int columnCount = rsm.getColumnCount();
      final String[] columnNames = new String[columnCount];
      for (int i = 0; i < columnCount; i++)
      {
        columnNames[i] = rsm.getColumnName(i + 1);
      }
      handleRowsHeader(columnNames);

      handleRowsBegin();
      if (options.isMergeRows() && columnCount > 1)
      {
        iterateRowsAndMerge(rows, columnNames);
      }
      else
      {
        iterateRows(rows, columnNames);
      }

      handleRowsEnd();
    }
    catch (final SQLException e)
    {
      throw new QueryExecutorException(e.getMessage(), e);
    }
  }

  /**
   * Handles metadata information.
   * 
   * @param databaseInfo
   *        Database info.
   */
  public void handleMetadata(final String databaseInfo)
  {
    if (!getNoInfo())
    {
      out.println(FormatUtils.repeat("-", FormatUtils.MAX_LINE_LENGTH));
      out.println(databaseInfo);
      out.println(FormatUtils.repeat("-", FormatUtils.MAX_LINE_LENGTH));
      out.flush();
    }
  }

  /**
   * Called to handle the row output. Handler to be implemented by
   * subclass.
   * 
   * @param columnNames
   *        Column names
   * @param columnData
   *        Column data
   * @throws QueryExecutorException
   *         On an exception
   */
  public abstract void handleRow(final String[] columnNames,
                                 final String[] columnData)
    throws QueryExecutorException;

  /**
   * Called to handle the beginning of row output. Handler to be
   * implemented by subclass.
   * 
   * @throws QueryExecutorException
   *         On an exception
   */
  public abstract void handleRowsBegin()
    throws QueryExecutorException;

  /**
   * Called to handle the end of row output. Handler to be implemented
   * by subclass.
   * 
   * @throws QueryExecutorException
   *         On an exception
   */
  public abstract void handleRowsEnd()
    throws QueryExecutorException;

  /**
   * Called to handle the header output. Handler to be implemented by
   * subclass.
   * 
   * @param columnNames
   *        Column names
   * @throws QueryExecutorException
   *         On an exception
   */
  public abstract void handleRowsHeader(final String[] columnNames)
    throws QueryExecutorException;

  boolean getNoFooter()
  {
    return options.getOutputOptions().isNoFooter();
  }

  boolean getNoHeader()
  {
    return options.getOutputOptions().isNoHeader();
  }

  boolean getNoInfo()
  {
    return options.getOutputOptions().isNoInfo();
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

  private void doHandleOneRow(final String[] columnNames,
                              final List<String> row,
                              final String lastColumnData)
    throws QueryExecutorException
  {
    if (row.size() == 0)
    {
      return;
    }
    final List<String> outputRow = new ArrayList<String>();
    // output
    outputRow.addAll(row);
    outputRow.add(lastColumnData);
    final String[] columnData = outputRow.toArray(new String[outputRow.size()]);
    handleRow(columnNames, columnData);
  }

  private void iterateRows(final ResultSet rows, final String[] columnNames)
    throws SQLException, QueryExecutorException
  {
    final int columnCount = columnNames.length;
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
      handleRow(columnNames, columnData);
    }
  }

  /**
   * @param rows
   * @param columnCount
   * @param columnNames
   * @throws SQLException
   * @throws QueryExecutorException
   */
  private void iterateRowsAndMerge(final ResultSet rows,
                                   final String[] columnNames)
    throws SQLException, QueryExecutorException
  {
    final int columnCount = columnNames.length;
    List<String> previousRow = new ArrayList<String>();
    List<String> currentRow;
    StringBuffer currentRowLastColumn = new StringBuffer();
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
        doHandleOneRow(columnNames, previousRow, currentRowLastColumn
          .toString());
        // reset
        currentRowLastColumn = new StringBuffer();
        // save the last column
        currentRowLastColumn.append(lastColumnDataString);
      }

      previousRow = currentRow;
    }
    // Dump the last row out
    doHandleOneRow(columnNames, previousRow, currentRowLastColumn.toString());
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
    String lobData = BINARY;
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
      lobData = new String(Utilities.readFully(in));
      return lobData;
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.FINE, "Could not read binary data", e);
      return lobData;
    }

  }
}
