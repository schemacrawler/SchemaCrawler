/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.crawl;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;

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
    throws SQLException
  {
    if (resultSet == null)
    {
      throw new SQLException("Cannot retrieve metadata for null results");
    }
    resultsMetaData = resultSet.getMetaData();
  }

  /**
   * Retrieves a list of columns from the results. There is no attempt
   * to share table objects, since the tables cannot have children that
   * are ResultColumns. Likewise, there is no attempt to share column
   * data types.
   * 
   * @return List of columns from the results
   * @throws SchemaCrawlerException
   *         On an exception
   */
  ResultsColumns retrieveResults()
    throws SQLException
  {
    final MutableResultsColumns resultColumns = new MutableResultsColumns("");
    final MutableDatabase database = new MutableDatabase("results");
    final int columnCount = resultsMetaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++)
    {
      final String catalogName = resultsMetaData.getCatalogName(i);
      final String schemaName = resultsMetaData.getSchemaName(i);
      String tableName = resultsMetaData.getTableName(i);
      if (Utility.isBlank(tableName))
      {
        tableName = "";
      }

      final Schema schema = database.addSchema(catalogName, schemaName);
      final MutableTable table = new MutableTable(schema, tableName);
      database.addTable(table);

      final String databaseSpecificTypeName = resultsMetaData
        .getColumnTypeName(i);
      final MutableColumnDataType columnDataType = new MutableColumnDataType(schema,
                                                                             databaseSpecificTypeName);
      columnDataType.setType(resultsMetaData.getColumnType(i),
                             resultsMetaData.getColumnClassName(i));
      columnDataType.setPrecision(resultsMetaData.getPrecision(i));
      final int scale = resultsMetaData.getScale(i);
      columnDataType.setMaximumScale(scale);
      columnDataType.setMinimumScale(scale);

      final String columnName = resultsMetaData.getColumnName(i);
      final MutableResultsColumn column = new MutableResultsColumn(table,
                                                                   columnName);
      column.setOrdinalPosition(i);
      column.setColumnDataType(columnDataType);

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

    return resultColumns;
  }
}
