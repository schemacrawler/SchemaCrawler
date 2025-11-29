/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaKey.INDEXES;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
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
import us.fatehi.utility.UtilityLogger;
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
        retrieveIndexesFromDataDictionary();
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving indexes");
        retrieveIndexesFromMetadata(allTables);
        break;

      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO, "Retrieving indexes, using fast data dictionary retrieval over schemas");
        retrieveIndexesOverSchemas();
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving indexes");
        break;
    }
  }

  /**
   * Retrieves index information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveIndexInformation() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(EXT_INDEXES)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional index information, since this was not requested");
      LOGGER.log(Level.FINE, "Indexes information SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving additional index information");

    final String name = "indexes for index information";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query extIndexesInformationSql = informationSchemaViews.getQuery(EXT_INDEXES);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(extIndexesInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("INDEX_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("INDEX_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String indexName = results.getString("INDEX_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (tableOptional.isEmpty()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, indexName));
          continue;
        }

        LOGGER.log(Level.FINER, new StringFormat("Retrieving index information <%s>", indexName));
        final MutableTable table = tableOptional.get();
        final Optional<MutableIndex> indexOptional = table.lookupIndex(indexName);
        if (indexOptional.isEmpty()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find index <%s.%s.%s.%s>",
                  catalogName, schemaName, tableName, indexName));
          continue;
        }

        final MutableIndex index = indexOptional.get();

        final String definition = results.getString("INDEX_DEFINITION");
        final String remarks = results.getString("REMARKS");

        index.setDefinition(definition);
        index.setRemarks(remarks);

        index.addAttributes(results.getAttributes());

        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve index information", e);
    }
    retrievalCounts.log();
  }

  private boolean createIndexForTable(final MutableTable table, final MetadataResultSet results) {
    // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
    String indexName = results.getString("INDEX_NAME");
    LOGGER.log(Level.FINE, new StringFormat("Retrieving index <%s.%s>", table, indexName));

    // Work-around PostgreSQL JDBC driver bugs by unquoting column names first
    // #3480 -
    // http://www.postgresql.org/message-id/200707231358.l6NDwlWh026230@wwwmaster.postgresql.org
    // #6253 -
    // http://www.postgresql.org/message-id/201110121403.p9CE3fsx039675@wwwmaster.postgresql.org
    final String columnName = results.getString("COLUMN_NAME");
    if (isBlank(columnName)) {
      return false;
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
    final String filterCondition = results.getString("FILTER_CONDITION");

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
          "SC_%s".formatted(Integer.toHexString(column.getFullName().hashCode()).toUpperCase());
    }

    final Optional<MutableIndex> indexOptional = table.lookupIndex(indexName);
    final MutableIndex index;
    if (indexOptional.isPresent()) {
      index = indexOptional.get();
    } else {
      index = new MutableIndex(table, indexName);
      table.addIndex(index);
    }
    index.withQuoting(getRetrieverConnection().getIdentifiers());

    final MutableIndexColumn indexColumn = new MutableIndexColumn(index, column);
    indexColumn.setKeyOrdinalPosition(ordinalPosition);
    indexColumn.setSortSequence(sortSequence);
    //
    index.addColumn(indexColumn);
    index.setUnique(uniqueIndex);
    index.setIndexType(type);
    index.setCardinality(cardinality);
    index.setPages(pages);
    index.setFilterCondition(filterCondition);
    index.addAttributes(results.getAttributes());

    return true;
  }

  private void retrieveIndexesFromDataDictionary() throws WrappedSQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(INDEXES)) {
      LOGGER.log(Level.FINE, "Extended indexes SQL statement was not provided");
      return;
    }
    final Query indexesSql = informationSchemaViews.getQuery(INDEXES);

    final String name = "indexes from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(indexesSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> optionalTable =
            lookupTable(catalogName, schemaName, tableName);
        if (optionalTable.isEmpty()) {
          continue;
        }
        final MutableTable table = optionalTable.get();
        final boolean added = createIndexForTable(table, results);
        retrievalCounts.countIfIncluded(added);
      }
      retrievalCounts.log();
    } catch (final SQLException e) {
      throw new WrappedSQLException(
          "Could not retrieve indexes from SQL:%n%s".formatted(indexesSql), e);
    }
  }

  private void retrieveIndexesFromMetadata(final NamedObjectList<MutableTable> allTables)
      throws SQLException {
    final String name = "indexes from metadata";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final MutableTable table : allTables) {
      LOGGER.log(Level.INFO, new StringFormat("Retrieving %s for %s", name, table.key()));

      final Schema tableSchema = table.getSchema();
      try (final Connection connection = getRetrieverConnection().getConnection(name);
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
        while (results.next()) {
          retrievalCounts.count();
          final boolean added = createIndexForTable(table, results);
          retrievalCounts.countIfIncluded(added);
        }
      } catch (final SQLException e) {
        new UtilityLogger(LOGGER)
            .logPossiblyUnsupportedSQLFeature(
                new StringFormat("Could not retrieve indexes for table <%s>", table), e);
      }
    }
    retrievalCounts.log();
  }

  private void retrieveIndexesOverSchemas() throws WrappedSQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(INDEXES)) {
      LOGGER.log(Level.FINE, "Extended indexes SQL statement was not provided");
      return;
    }
    final Query indexesSql = informationSchemaViews.getQuery(INDEXES);

    final Collection<Schema> schemas = catalog.getSchemas();
    final String name = "indexes from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(indexesSql, statement, getLimitMap(schema)); ) {
        final String catalogName = schema.getCatalogName();
        while (results.next()) {
          retrievalCounts.count(schema.key());
          // final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
          final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
          final String tableName = results.getString("TABLE_NAME");

          final Optional<MutableTable> optionalTable =
              lookupTable(catalogName, schemaName, tableName);
          if (optionalTable.isEmpty()) {
            continue;
          }
          final MutableTable table = optionalTable.get();
          final boolean added = createIndexForTable(table, results);
          retrievalCounts.countIfIncluded(schema.key(), added);
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not retrieve indexes for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }
}
