/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.base.BaseTabularFormatter;
import schemacrawler.tools.text.utility.TextFormattingHelper.DocumentHeaderType;
import schemacrawler.tools.text.utility.html.Alignment;
import schemacrawler.tools.traversal.DataTraversalHandler;
import schemacrawler.utility.Query;

/**
 * Text formatting of data.
 *
 * @author Sualeh Fatehi
 */
final class DataTextFormatter
  extends BaseTabularFormatter<OperationOptions>
  implements DataTraversalHandler
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
   * @see schemacrawler.tools.traversal.DataTraversalHandler#end()
   */
  @Override
  public void end()
    throws SchemaCrawlerException
  {
    if (operation == Operation.count)
    {
      formattingHelper.writeObjectEnd();
    }

    super.end();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.DataTraversalHandler#handleData(schemacrawler.utility.Query,
   *      java.sql.ResultSet)
   */
  @Override
  public void handleData(final Query query, final ResultSet rows)
    throws SchemaCrawlerException
  {
    String title;
    if (query != null)
    {
      title = query.getName();
    }
    else
    {
      title = "";
    }

    handleData(title, rows);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.tools.traversal.DataTraversalHandler#handleData(schemacrawler.schema.Table,
   *      java.sql.ResultSet)
   */
  @Override
  public void handleData(final Table table, final ResultSet rows)
    throws SchemaCrawlerException
  {
    final String tableName;
    if (table != null)
    {
      if (options.isShowUnqualifiedNames())
      {
        tableName = table.getName();
      }
      else
      {
        tableName = table.getFullName();
      }
    }
    else
    {
      tableName = "";
    }

    handleData(tableName, rows);
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
    formattingHelper.writeNameValueRow(title, message, Alignment.right);
  }

  private void handleData(final String title, final ResultSet rows)
    throws SchemaCrawlerException
  {
    if (rows == null)
    {
      return;
    }

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
      formattingHelper.println();
      formattingHelper.println();
      formattingHelper.writeObjectStart();
      formattingHelper.writeObjectNameRow("", title, "", Color.white);
      try
      {
        final DataResultSet dataRows = new DataResultSet(rows,
                                                         options.isShowLobs());

        formattingHelper.writeRowHeader(dataRows.getColumnNames());

        iterateRows(dataRows);
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerException(e.getMessage(), e);
      }
      formattingHelper.writeObjectEnd();
    }

    dataBlockCount++;
  }

  private void iterateRows(final DataResultSet dataRows)
    throws SQLException
  {
    while (dataRows.next())
    {
      final List<Object> currentRow = dataRows.row();
      final Object[] columnData = currentRow
        .toArray(new Object[currentRow.size()]);
      formattingHelper.writeRow(columnData);
    }
  }

  private void printHeader()
  {
    if (operation != null)
    {
      formattingHelper.writeHeader(DocumentHeaderType.subTitle,
                                   operation.getDescription());
    }
    else
    {
      formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Query");
    }

    if (operation == Operation.count)
    {
      formattingHelper.writeObjectStart();
      formattingHelper
        .writeObjectNameRow("", operation.getDescription(), "", Color.white);
    }
  }
}
