/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.schema.DataTypeType.user_defined;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Retriever;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.utility.JavaSqlTypes;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the details about a result set. */
@Retriever
final class ResultsRetriever {

  private static final Logger LOGGER = Logger.getLogger(ResultsRetriever.class.getName());

  private final ResultSetMetaData resultsMetaData;
  private final JavaSqlTypes javaSqlTypes;
  private final NamedObjectList<Schema> schemas;
  private final NamedObjectList<Table> tables;

  ResultsRetriever(final ResultSet resultSet) throws SQLException {
    // NOTE: Do not check if the result set is closed, since some JDBC
    // drivers like SQLite may not work
    requireNonNull(resultSet, "Cannot retrieve metadata for null results");
    resultsMetaData = resultSet.getMetaData();
    javaSqlTypes = new JavaSqlTypes();
    schemas = new NamedObjectList<>();
    tables = new NamedObjectList<>();
  }

  /**
   * Retrieves a list of columns from the results. There is no attempt to share table objects, since
   * the tables cannot have children that are ResultColumns. Likewise, there is no attempt to share
   * column data types.
   *
   * @return List of columns from the results
   */
  ResultsColumns retrieveResults() throws SQLException {

    final MutableResultsColumns resultColumns = new MutableResultsColumns("");
    final int columnCount = resultsMetaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      final int columnIndex = i;

      final String catalogName =
          execute("catalog name", () -> resultsMetaData.getCatalogName(columnIndex));
      final String schemaName =
          execute("schema name", () -> resultsMetaData.getSchemaName(columnIndex));

      final Schema schema =
          schemas
              .lookup(new NamedObjectKey(catalogName, schemaName))
              .orElseGet(
                  () -> {
                    final SchemaReference newSchema = new SchemaReference(catalogName, schemaName);
                    schemas.add(newSchema);
                    return newSchema;
                  });

      final String tableName =
          trimToEmpty(execute("table name", () -> resultsMetaData.getTableName(columnIndex)));

      final Table table =
          tables
              .lookup(schema, tableName)
              .orElseGet(
                  () -> {
                    final Table newTable = new TablePartial(schema, tableName);
                    tables.add(newTable);
                    return newTable;
                  });

      final String columnName =
          execute("column name", () -> resultsMetaData.getColumnName(columnIndex));
      final String columnLabel =
          execute("column label", () -> resultsMetaData.getColumnLabel(columnIndex));

      final MutableResultsColumn column = new MutableResultsColumn(table, columnName, columnLabel);
      setColumnDataType(columnIndex, column);
      retrieveAdditionalColumnData(columnIndex, column);

      resultColumns.addColumn(column);
    }

    return resultColumns;
  }

  private String execute(final String resultsColumnField, final Callable<String> getResultsColumn) {
    try {
      return getResultsColumn.call();
    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING,
          e,
          new StringFormat("Could not retrieve results column field, %s", resultsColumnField));
      return null;
    }
  }

  private void retrieveAdditionalColumnData(
      final int columnIndex, final MutableResultsColumn column) {
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
          e,
          new StringFormat(
              "Could not retrieve results column additional data for %s (%s)",
              column, column.getLabel()));
    }
  }

  private void setColumnDataType(final int columnIndex, final MutableResultsColumn column) {
    final Schema schema = column.getSchema();
    try {
      final String databaseSpecificTypeName = resultsMetaData.getColumnTypeName(columnIndex);
      final int javaSqlType = resultsMetaData.getColumnType(columnIndex);
      final String columnClassName = resultsMetaData.getColumnClassName(columnIndex);
      final MutableColumnDataType columnDataType =
          new MutableColumnDataType(schema, databaseSpecificTypeName, user_defined);
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
          e,
          new StringFormat(
              "Could not retrieve results column data type for %s (%s)",
              column, column.getLabel()));

      final MutableColumnDataType unknownColumnDataType =
          new MutableColumnDataType(schema, "<unknown>", user_defined);
      unknownColumnDataType.setJavaSqlType(JavaSqlType.UNKNOWN);
      //
      column.setColumnDataType(unknownColumnDataType);
    }
  }
}
