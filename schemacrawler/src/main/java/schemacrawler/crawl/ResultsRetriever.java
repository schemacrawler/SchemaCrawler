/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import schemacrawler.SchemaCrawlerException;
import schemacrawler.schema.ResultsColumns;

/**
 * A retriever uses database metadata to get the details about a result
 * set.
 * 
 * @author Sualeh Fatehi
 */
final class ResultsRetriever
  extends AbstractRetriever
{

  private final ResultSetMetaData resultsMetaData;

  ResultsRetriever(final ResultSet resultSet)
    throws SchemaCrawlerException
  {
    if (resultSet == null)
    {
      throw new SchemaCrawlerException("Cannot retrieve metadata for null results");
    }
    try
    {
      resultsMetaData = resultSet.getMetaData();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot retrieve metadata for results",
                                       e);
    }
  }

  /**
   * Retrieves a list of columns from the results. There is no attempt
   * to share table objects, since the tables cannot have children that
   * are ResultColumns. Likewise, there is no attempt to share column
   * data types.
   * 
   * @throws SQLException
   *         On a SQL exception
   */
  ResultsColumns retrieveResults()
    throws SchemaCrawlerException
  {
    final MutableResultsColumns resultColumns = new MutableResultsColumns("");

    try
    {
      final int columnCount = resultsMetaData.getColumnCount();
      for (int i = 1; i <= columnCount; i++)
      {
        final String catalogName = resultsMetaData.getCatalogName(i);
        final String schemaName = resultsMetaData.getSchemaName(i);
        String tableName = resultsMetaData.getTableName(i);
        if (tableName == null)
        {
          tableName = "";
        }
        final MutableTable table = new MutableTable(catalogName,
                                                    schemaName,
                                                    tableName);

        final String databaseSpecificTypeName = resultsMetaData
          .getColumnTypeName(i);
        final MutableColumnDataType columnDataType = new MutableColumnDataType(databaseSpecificTypeName);
        columnDataType.setType(resultsMetaData.getColumnType(i));
        columnDataType.setTypeClassName(resultsMetaData.getColumnClassName(i));
        columnDataType.setPrecision(resultsMetaData.getPrecision(i));
        final int scale = resultsMetaData.getScale(i);
        columnDataType.setMaximumScale(scale);
        columnDataType.setMinimumScale(scale);

        final String columnName = resultsMetaData.getColumnName(i);
        final MutableResultsColumn column = new MutableResultsColumn(columnName,
                                                                     table);
        column.setOrdinalPosition(i);
        column.setType(columnDataType);

        column.setLabel(resultsMetaData.getColumnLabel(i));
        column.setDisplaySize(resultsMetaData.getColumnDisplaySize(i));

        final boolean isNullable = resultsMetaData.isNullable(i) == ResultSetMetaData.columnNullable;
        column.setAutoIncrement(resultsMetaData.isAutoIncrement(i));
        column.setCaseSensitive(resultsMetaData.isCaseSensitive(i));
        column.setCurrency(resultsMetaData.isCurrency(i));
        column.setDefinitelyWritable(resultsMetaData.isDefinitelyWritable(i));
        column.setNullable(isNullable);
        column.setReadOnly(resultsMetaData.isReadOnly(i));
        column.setSearchable(resultsMetaData.isSearchable(i));
        column.setSigned(resultsMetaData.isSigned(i));
        column.setWritable(resultsMetaData.isWritable(i));

        resultColumns.addColumn(column);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot retrieve metadata for results",
                                       e);
    }

    return resultColumns;
  }
}
