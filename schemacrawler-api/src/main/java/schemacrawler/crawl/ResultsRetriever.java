/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.JavaSqlTypes;

/**
 * A retriever uses database metadata to get the details about a result
 * set.
 *
 * @author Sualeh Fatehi
 */
final class ResultsRetriever
  implements Retriever
{

  private final ResultSetMetaData resultsMetaData;

  ResultsRetriever(final ResultSet resultSet)
    throws SQLException
  {
    requireNonNull(resultSet, "Cannot retrieve metadata for null results");
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
    final JavaSqlTypes javaSqlTypes = new JavaSqlTypes();
    final MutableResultsColumns resultColumns = new MutableResultsColumns("");
    final MutableCatalog catalog = new MutableCatalog("results");
    final int columnCount = resultsMetaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++)
    {
      final String catalogName = resultsMetaData.getCatalogName(i);
      final String schemaName = resultsMetaData.getSchemaName(i);
      String tableName = resultsMetaData.getTableName(i);
      if (isBlank(tableName))
      {
        tableName = "";
      }

      final Schema schema = catalog.addSchema(catalogName, schemaName);
      final MutableTable table = new MutableTable(schema, tableName);
      catalog.addTable(table);

      final String databaseSpecificTypeName = resultsMetaData
        .getColumnTypeName(i);
      final int javaSqlType = resultsMetaData.getColumnType(i);
      final String columnClassName = resultsMetaData.getColumnClassName(i);
      final MutableColumnDataType columnDataType = new MutableColumnDataType(schema,
                                                                             databaseSpecificTypeName);
      columnDataType.setJavaSqlType(javaSqlTypes.get(javaSqlType));
      columnDataType.setTypeMappedClass(columnClassName);
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

      final boolean isNullable = resultsMetaData
        .isNullable(i) == ResultSetMetaData.columnNullable;
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
