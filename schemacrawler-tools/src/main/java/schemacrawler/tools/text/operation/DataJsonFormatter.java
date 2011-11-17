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
import schemacrawler.tools.text.base.BaseJsonFormatter;
import schemacrawler.tools.text.utility.org.json.JSONArray;
import schemacrawler.tools.text.utility.org.json.JSONException;
import schemacrawler.tools.text.utility.org.json.JSONObject;
import schemacrawler.tools.traversal.DataTraversalHandler;

/**
 * Text formatting of data.
 * 
 * @author Sualeh Fatehi
 */
final class DataJsonFormatter
  extends BaseJsonFormatter<OperationOptions>
  implements DataTraversalHandler
{

  private final Operation operation;
  private final JSONArray jsonDataArray;

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
  DataJsonFormatter(final Operation operation,
                    final OperationOptions options,
                    final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(options, /* printVerboseDatabaseInfo */false, outputOptions);
    this.operation = operation;

    jsonDataArray = new JSONArray();
    try
    {
      if (operation != null)
      {
        jsonRoot.put("description", operation.getDescription());
      }
      jsonRoot.put("data", jsonDataArray);
    }
    catch (JSONException e)
    {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.traversal.DataTraversalHandler#handleData(java.lang.String,
   *      java.sql.ResultSet)
   */
  public void handleData(final String title, final ResultSet rows)
    throws SchemaCrawlerException
  {
    try
    {
      JSONObject jsonData = new JSONObject();
      jsonData.put("title", title);

      if (operation == Operation.count)
      {
        long aggregate = handleAggregateOperationForTable(title, rows);
        jsonData.put("value", aggregate);
      }
      else
      {
        try
        {
          final DataResultSet dataRows = new DataResultSet(rows,
                                                           options.isShowLobs());

          jsonData.put("columnNames", new JSONArray(dataRows.getColumnNames()));

          JSONArray jsonRows;
          if (options.isMergeRows() && dataRows.width() > 1)
          {
            jsonRows = iterateRowsAndMerge(dataRows);
          }
          else
          {
            jsonRows = iterateRows(dataRows);
          }

          jsonData.put("rows", jsonRows);
        }
        catch (final SQLException e)
        {
          throw new SchemaCrawlerException(e.getMessage(), e);
        }
      }

      jsonDataArray.put(jsonData);
    }
    catch (final JSONException e)
    {
      throw new SchemaCrawlerException("Could not convert data to JSON", e);
    }

  }

  private JSONArray doHandleOneRow(final List<String> row,
                                   final String lastColumnData)
  {
    if (row.isEmpty())
    {
      return new JSONArray();
    }
    final List<String> outputRow = new ArrayList<String>();
    outputRow.addAll(row);
    outputRow.add(lastColumnData);
    return new JSONArray(outputRow);
  }

  /**
   * Handles an aggregate operation, such as a count, for a given table.
   * 
   * @param title
   *        Title
   * @param results
   *        Results
   */
  private long handleAggregateOperationForTable(final String title,
                                                final ResultSet results)
    throws SchemaCrawlerException
  {
    try
    {
      long aggregate = 0;
      if (results.next())
      {
        aggregate = results.getLong(1);
      }
      return aggregate;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Could not obtain aggregate data", e);
    }
  }

  private JSONArray iterateRows(final DataResultSet dataRows)
    throws SQLException
  {
    final JSONArray jsonRows = new JSONArray();
    while (dataRows.next())
    {
      final List<String> currentRow = dataRows.row();
      jsonRows.put(new JSONArray(currentRow));
    }
    return jsonRows;
  }

  private JSONArray iterateRowsAndMerge(final DataResultSet dataRows)
    throws SQLException
  {
    final JSONArray jsonRows = new JSONArray();
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
        final JSONArray jsonRow = doHandleOneRow(previousRow,
                                                 currentRowLastColumn
                                                   .toString());
        jsonRows.put(jsonRow);
        // reset
        currentRowLastColumn = new StringBuilder();
        // save the last column
        currentRowLastColumn.append(lastColumnDataString);
      }

      previousRow = currentRow;
    }
    // Dump the last row out
    final JSONArray jsonRow = doHandleOneRow(previousRow,
                                             currentRowLastColumn.toString());
    jsonRows.put(jsonRow);

    return jsonRows;
  }
}
