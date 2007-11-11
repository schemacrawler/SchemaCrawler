/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

import schemacrawler.schema.ResultColumns;
import sf.util.Utilities;

/**
 * TableRetriever uses database metadata to get the details about the
 * schema.
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
    catch (SQLException e)
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
  ResultColumns retrieveResults()
    throws SchemaCrawlerException
  {
    MutableResultColumns resultColumns = new MutableResultColumns("");

    try
    {
      int columnCount = resultsMetaData.getColumnCount();
      for (int i = 0; i < columnCount; i++)
      {
        String catalogName = resultsMetaData.getCatalogName(i);
        String schemaName = resultsMetaData.getSchemaName(i);
        String tableName = resultsMetaData.getTableName(i);
        MutableTable table = null;
        if (Utilities.isBlank(tableName))
        {
          table = new MutableTable(catalogName, schemaName, tableName);
        }

        String databaseSpecificTypeName = resultsMetaData.getColumnTypeName(i);
        MutableColumnDataType columnDataType = new MutableColumnDataType(databaseSpecificTypeName);
        columnDataType.setType(resultsMetaData.getColumnType(i));
        columnDataType.setTypeClassName(resultsMetaData.getColumnClassName(i));
        columnDataType.setPrecision(resultsMetaData.getPrecision(i));
        int scale = resultsMetaData.getScale(i);
        columnDataType.setMaximumScale(scale);
        columnDataType.setMinimumScale(scale);

        String columnName = resultsMetaData.getColumnName(i);
        MutableResultsColumn column = new MutableResultsColumn(columnName,
                                                               table);
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
    catch (SQLException e)
    {
      throw new SchemaCrawlerException("Cannot retrieve metadata for results",
                                       e);
    }

    return resultColumns;
  }

}
