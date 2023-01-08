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
import static schemacrawler.schemacrawler.InformationSchemaKey.INDEXES;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.IndexColumnSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.WrappedSQLException;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the details about the database tables. */
final class IndexRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(IndexRetriever.class.getName());

  IndexRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options) {
    super(retrieverConnection, catalog, options);
  }

  void retrieveIndexes(final NamedObjectList<MutableTable> allTables) throws SQLException {
    requireNonNull(allTables, "No tables provided");

    switch (getRetrieverConnection().get(indexesRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(Level.INFO, "Retrieving indexes, using fast data dictionary retrieval");
        retrieveIndexesFromDataDictionary(allTables);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving indexes");
        retrieveIndexesFromMetadata(allTables);
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving indexes");
        break;
    }
  }

  private void createIndexes(final MutableTable table, final MetadataResultSet results)
      throws SQLException {
    while (results.next()) {
      createIndexForTable(table, results);
    }
  }

  private void createIndexForTable(final MutableTable table, final MetadataResultSet results) {
    // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
    String indexName = results.getString("INDEX_NAME");
    LOGGER.log(Level.FINE, new StringFormat("Retrieving index <%s.%s>", table, indexName));

    // Work-around PostgreSQL JDBC driver bugs by unquoting column
    // names first
    // #3480 -
    // http://www.postgresql.org/message-id/200707231358.l6NDwlWh026230@wwwmaster.postgresql.org
    // #6253 -
    // http://www.postgresql.org/message-id/201110121403.p9CE3fsx039675@wwwmaster.postgresql.org
    final String columnName = results.getString("COLUMN_NAME");
    if (isBlank(columnName)) {
      return;
    }
    LOGGER.log(
        Level.FINE,
        new StringFormat("Retrieving index column <%s.%s.%s>", table, indexName, columnName));

    final boolean uniqueIndex = !results.getBoolean("NON_UNIQUE");
    final IndexType type = results.getEnumFromId("TYPE", IndexType.unknown);
    final int ordinalPosition = results.getShort("ORDINAL_POSITION", (short) 0);
    final IndexColumnSortSequence sortSequence =
        IndexColumnSortSequence.valueOfFromCode(results.getString("ASC_OR_DESC"));
    final long cardinality = results.getLong("CARDINALITY", 0L);
    final long pages = results.getLong("PAGES", 0L);

    final Column column;
    final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
    if (columnOptional.isPresent()) {
      final MutableColumn mutableColumn = columnOptional.get();
      mutableColumn.markAsPartOfIndex();
      if (uniqueIndex) {
        mutableColumn.markAsPartOfUniqueIndex();
      }
      column = mutableColumn;
    } else {
      // Indexes may have pseudo-columns, that are not part of the table
      // for example, Oracle function-based indexes have columns from
      // the result of a function
      column = new ColumnPartial(table, columnName);
    }

    if (isBlank(indexName)) {
      indexName =
          String.format(
              "SC_%s", Integer.toHexString(column.getFullName().hashCode()).toUpperCase());
    }

    final Optional<MutableIndex> indexOptional = table.lookupIndex(indexName);
    final MutableIndex index;
    if (indexOptional.isPresent()) {
      index = indexOptional.get();
    } else {
      index = new MutableIndex(table, indexName);
      table.addIndex(index);
    }

    final MutableIndexColumn indexColumn = new MutableIndexColumn(index, column);
    indexColumn.setKeyOrdinalPosition(ordinalPosition);
    indexColumn.setSortSequence(sortSequence);
    //
    index.addColumn(indexColumn);
    index.setUnique(uniqueIndex);
    index.setIndexType(type);
    index.setCardinality(cardinality);
    index.setPages(pages);
    index.addAttributes(results.getAttributes());
  }

  private void retrieveIndexesFromDataDictionary(final NamedObjectList<MutableTable> allTables)
      throws WrappedSQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(INDEXES)) {
      LOGGER.log(Level.FINE, "Extended indexes SQL statement was not provided");
      return;
    }

    final Query indexesSql = informationSchemaViews.getQuery(INDEXES);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(indexesSql, statement, getSchemaInclusionRule()); ) {
      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> optionalTable =
            lookupTable(catalogName, schemaName, tableName);
        if (!optionalTable.isPresent()) {
          continue;
        }
        final MutableTable table = optionalTable.get();
        createIndexForTable(table, results);
      }
    } catch (final SQLException e) {
      throw new WrappedSQLException(
          String.format("Could not retrieve indexes from SQL:%n%s", indexesSql), e);
    }
  }

  private void retrieveIndexesFromMetadata(final NamedObjectList<MutableTable> allTables)
      throws SQLException {
    for (final MutableTable table : allTables) {
      final Schema tableSchema = table.getSchema();
      try (final Connection connection = getRetrieverConnection().getConnection();
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection
                      .getMetaData()
                      .getIndexInfo(
                          tableSchema.getCatalogName(),
                          tableSchema.getName(),
                          table.getName(),
                          false /* return indices regardless of whether unique or not */,
                          true /* approximate - reflect approximate or out of data values */),
                  "DatabaseMetaData::getIndexInfo"); ) {
        createIndexes(table, results);
      } catch (final SQLException e) {
        logPossiblyUnsupportedSQLFeature(
            new StringFormat("Could not retrieve indexes for table <%s>", table), e);
      }
    }
  }
}
