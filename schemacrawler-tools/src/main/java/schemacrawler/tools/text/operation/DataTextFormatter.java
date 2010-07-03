/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.types.Types;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseFormatter;
import schemacrawler.tools.text.util.TextFormattingHelper.DocumentHeaderType;

/**
 * Text formatting of data.
 * 
 * @author Sualeh Fatehi
 */
final class DataTextFormatter
  extends BaseFormatter<OperationOptions>
{

  private static final Logger LOGGER = Logger.getLogger(DataTextFormatter.class
    .getName());

  private static final String NULL = "<null>";
  private static final String BINARY = "<binary>";

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

  private String convertColumnDataToString(final ResultsColumn[] resultsColumns,
                                           final ResultSet rows,
                                           final int i)
    throws SQLException
  {
    final int javaSqlType = resultsColumns[i].getType().getType();
    String columnDataString;
    if (javaSqlType == Types.CLOB)
    {
      final Clob clob = rows.getClob(i + 1);
      if (rows.wasNull() || clob == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readClob(clob);
      }
    }
    else if (javaSqlType == Types.NCLOB)
    {
      final NClob nClob = rows.getNClob(i + 1);
      if (rows.wasNull() || nClob == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readClob(nClob);
      }
    }
    else if (javaSqlType == Types.BLOB)
    {
      final Blob blob = rows.getBlob(i + 1);
      if (rows.wasNull() || blob == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readBlob(blob);
      }
    }
    else if (javaSqlType == Types.LONGVARBINARY)
    {
      final InputStream stream = rows.getBinaryStream(i + 1);
      if (rows.wasNull() || stream == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readStream(stream);
      }
    }
    else if (javaSqlType == Types.LONGNVARCHAR
             || javaSqlType == Types.LONGVARCHAR)
    {
      final InputStream stream = rows.getAsciiStream(i + 1);
      if (rows.wasNull() || stream == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readStream(stream);
      }
    }
    else
    {
      final Object columnData = rows.getObject(i + 1);
      if (rows.wasNull() || columnData == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = columnData.toString();
      }
    }
    return columnDataString;
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

  private void iterateRows(final ResultsColumn[] resultsColumns,
                           final ResultSet rows)
    throws SQLException
  {
    List<String> currentRow;
    while (rows.next())
    {
      final int columnCount = resultsColumns.length;
      currentRow = new ArrayList<String>(columnCount);
      for (int i = 0; i < columnCount; i++)
      {
        final String columnDataString = convertColumnDataToString(resultsColumns,
                                                                  rows,
                                                                  i);
        currentRow.add(columnDataString);
      }
      final String[] columnData = currentRow.toArray(new String[currentRow
        .size()]);
      out.println(formattingHelper.createRow(columnData));
    }
  }

  private void iterateRowsAndMerge(final ResultsColumn[] resultsColumns,
                                   final ResultSet rows)
    throws SQLException
  {
    final int columnCount = resultsColumns.length;
    List<String> previousRow = new ArrayList<String>();
    List<String> currentRow;
    StringBuilder currentRowLastColumn = new StringBuilder();
    // write out the data
    while (rows.next())
    {
      currentRow = new ArrayList<String>(columnCount - 1);
      for (int i = 0; i < columnCount - 1; i++)
      {
        final String columnDataString = convertColumnDataToString(resultsColumns,
                                                                  rows,
                                                                  i);
        currentRow.add(columnDataString);
      }
      final String lastColumnDataString = convertColumnDataToString(resultsColumns,
                                                                    rows,
                                                                    resultsColumns.length - 1);
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
      out.println(formattingHelper
        .createObjectStart(operation.getDescription()));
    }
  }

  private String readBlob(final Blob blob)
  {
    if (blob == null)
    {
      return NULL;
    }
    else if (options.isShowLobs())
    {
      InputStream in = null;
      String lobData;
      try
      {
        try
        {
          in = new BufferedInputStream(blob.getBinaryStream());
        }
        catch (final SQLFeatureNotSupportedException e)
        {
          in = null;
        }

        if (in != null)
        {
          lobData = sf.util.Utility.readFully(in);
        }
        else
        {
          lobData = BINARY;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read BLOB data", e);
        lobData = BINARY;
      }
      return lobData;
    }
    else
    {
      return BINARY;
    }
  }

  private String readClob(final Clob clob)
  {
    if (clob == null)
    {
      return NULL;
    }
    else if (options.isShowLobs())
    {
      Reader rdr = null;
      String lobData;
      try
      {
        try
        {
          rdr = new BufferedReader(new InputStreamReader(clob.getAsciiStream()));
        }
        catch (final SQLFeatureNotSupportedException e)
        {
          rdr = null;
        }
        if (rdr == null)
        {
          try
          {
            rdr = new BufferedReader(clob.getCharacterStream());
          }
          catch (final SQLFeatureNotSupportedException e)
          {
            rdr = null;
          }
        }

        if (rdr != null)
        {
          lobData = sf.util.Utility.readFully(rdr);
        }
        else
        {
          lobData = BINARY;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read CLOB data", e);
        lobData = BINARY;
      }
      return lobData;
    }
    else
    {
      return BINARY;
    }
  }

  /**
   * Reads data from an input stream into a string. Default system
   * encoding is assumed.
   * 
   * @param columnData
   *        Column data object returned by JDBC
   * @return A string with the contents of the LOB
   */
  private String readStream(final InputStream stream)
  {
    if (stream == null)
    {
      return NULL;
    }
    else if (options.isShowLobs())
    {
      final BufferedInputStream in = new BufferedInputStream(stream);
      final String lobData = sf.util.Utility.readFully(in);
      return lobData;
    }
    else
    {
      return BINARY;
    }
  }

  void begin()
  {
    if (!outputOptions.isNoHeader())
    {
      out.println(formattingHelper.createDocumentStart());
    }
  }

  void end()
  {
    if (operation == Operation.count)
    {
      out.println(formattingHelper.createObjectEnd());
    }

    if (!outputOptions.isNoFooter())
    {
      out.println(formattingHelper.createDocumentEnd());
    }
    out.flush();
    //
    outputOptions.closeOutputWriter(out);
  }

  void handleData(final String title, final ResultSet rows)
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
        final ResultsColumn[] resultsColumns = SchemaCrawler
          .getResultColumns(rows).getColumns();

        final int columnCount = resultsColumns.length;
        final String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++)
        {
          columnNames[i] = resultsColumns[i].getName();
        }
        out.println(formattingHelper.createRowHeader(columnNames));

        if (options.isMergeRows() && columnCount > 1)
        {
          iterateRowsAndMerge(resultsColumns, rows);
        }
        else
        {
          iterateRows(resultsColumns, rows);
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

}
