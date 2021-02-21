/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static us.fatehi.utility.Utility.isBlank;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.Retriever;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.JavaSqlTypes;

/**
 * A retriever uses database metadata to get the details about a result set.
 *
 * @author Sualeh Fatehi
 */
@Retriever
final class ResultsRetriever {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(ResultsRetriever.class.getName());

  private final ResultSetMetaData resultsMetaData;

  ResultsRetriever(final ResultSet resultSet) throws SQLException {
    // NOTE: Do not check if the result set is closed, since some JDBC
    // drivers like SQLite may not work
    requireNonNull(resultSet, "Cannot retrieve metadata for null results");
    resultsMetaData = resultSet.getMetaData();
  }

  /**
   * Retrieves a list of columns from the results. There is no attempt to share table objects, since
   * the tables cannot have children that are ResultColumns. Likewise, there is no attempt to share
   * column data types.
   *
   * @return List of columns from the results
   * @throws SchemaCrawlerException On an exception
   */
  ResultsColumns retrieveResults() throws SQLException {
    final JavaSqlTypes javaSqlTypes = new JavaSqlTypes();
    final MutableResultsColumns resultColumns = new MutableResultsColumns("");
    final MutableCatalog catalog = new MutableCatalog("results");
    final int columnCount = resultsMetaData.getColumnCount();
    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
      final String catalogName = resultsMetaData.getCatalogName(columnIndex);
      final String schemaName = resultsMetaData.getSchemaName(columnIndex);
      String tableName = resultsMetaData.getTableName(columnIndex);
      if (isBlank(tableName)) {
        tableName = "";
      }

      final Schema schema = catalog.addSchema(catalogName, schemaName);
      final MutableTable table = new MutableTable(schema, tableName);
      catalog.addTable(table);

      final String columnName = resultsMetaData.getColumnName(columnIndex);
      final String columnLabel = resultsMetaData.getColumnLabel(columnIndex);
      final MutableResultsColumn column = new MutableResultsColumn(table, columnName, columnLabel);

      try {
        final String databaseSpecificTypeName = resultsMetaData.getColumnTypeName(columnIndex);
        final int javaSqlType = resultsMetaData.getColumnType(columnIndex);
        final String columnClassName = resultsMetaData.getColumnClassName(columnIndex);
        final MutableColumnDataType columnDataType =
            new MutableColumnDataType(schema, databaseSpecificTypeName);
        columnDataType.setJavaSqlType(javaSqlTypes.valueOf(javaSqlType));
        columnDataType.setTypeMappedClass(columnClassName);
        columnDataType.setPrecision(resultsMetaData.getPrecision(columnIndex));
        final int scale = resultsMetaData.getScale(columnIndex);
        columnDataType.setMaximumScale(scale);
        columnDataType.setMinimumScale(scale);
        //
        column.setColumnDataType(columnDataType);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            String.format(
                "Could not retrieve column data type for %s (%s)", column, column.getLabel()),
            e);

        final MutableColumnDataType unknownColumnDataType =
            new MutableColumnDataType(schema, "<unknown>");
        unknownColumnDataType.setJavaSqlType(JavaSqlType.UNKNOWN);
        //
        column.setColumnDataType(unknownColumnDataType);
      }

      try {
        final boolean isNullable =
            resultsMetaData.isNullable(columnIndex) == ResultSetMetaData.columnNullable;

        column.setOrdinalPosition(columnIndex);
        column.setDisplaySize(resultsMetaData.getColumnDisplaySize(columnIndex));
        column.setAutoIncrement(resultsMetaData.isAutoIncrement(columnIndex));
        column.setCaseSensitive(resultsMetaData.isCaseSensitive(columnIndex));
        column.setCurrency(resultsMetaData.isCurrency(columnIndex));
        column.setDefinitelyWritable(resultsMetaData.isDefinitelyWritable(columnIndex));
        column.setNullable(isNullable);
        column.setReadOnly(resultsMetaData.isReadOnly(columnIndex));
        column.setSearchable(resultsMetaData.isSearchable(columnIndex));
        column.setSigned(resultsMetaData.isSigned(columnIndex));
        column.setWritable(resultsMetaData.isWritable(columnIndex));
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            String.format(
                "Could not retrieve additional column data for %s (%s)", column, column.getLabel()),
            e);
      }

      resultColumns.addColumn(column);
    }

    return resultColumns;
  }
}
