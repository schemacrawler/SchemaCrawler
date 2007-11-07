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
import java.util.logging.Logger;

/**
 * TableRetriever uses database metadata to get the details about the
 * schema.
 * 
 * @author Sualeh Fatehi
 */
final class ResultsRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(ResultsRetriever.class
    .getName());

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
   * Retrieves a list of columns from the database, for the table
   * specified.
   * 
   * @param table
   *        Table for which data is required.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveResults(final MutableResults results)
    throws SQLException
  {
    int columnCount = resultsMetaData.getColumnCount();
    for (int i = 0; i < columnCount; i++)
    {
      MutableResultsColumn column = new MutableResultsColumn(null, null);

      // getCatalogName(i)
      // getColumnClassName(i)
      // getColumnDisplaySize(i)
      // getColumnLabel(i)
      // getColumnName(i)
      // getColumnType(i)
      // getColumnTypeName(i)
      // getPrecision(i)
      // getScale(i)
      // getSchemaName(i)
      // getTableName(i)

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
    }

  }

  private MutableColumn lookupOrCreateColumn(final NamedObjectList<MutableTable> tables,
                                             final String schema,
                                             final String tableName,
                                             final String columnName)
  {

    MutableColumn column = null;
    MutableTable table = tables.lookup(tableName);
    if (table != null)
    {
      column = table.lookupColumn(columnName);
    }
    if (column == null)
    {
      final String catalog = getRetrieverConnection().getCatalog();
      table = new MutableTable(catalog, schema, tableName);
      column = new MutableColumn(columnName, table);
    }
    return column;
  }

}
