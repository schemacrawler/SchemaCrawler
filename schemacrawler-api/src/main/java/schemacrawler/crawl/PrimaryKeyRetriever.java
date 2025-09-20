/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.InformationSchemaKey.PRIMARY_KEYS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.utility.MetaDataUtility.isView;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.WrappedSQLException;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the details about the database tables. */
final class PrimaryKeyRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(PrimaryKeyRetriever.class.getName());

  PrimaryKeyRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  void retrievePrimaryKeys(final NamedObjectList<MutableTable> allTables) throws SQLException {
    requireNonNull(allTables, "No tables provided");

    switch (getRetrieverConnection().get(primaryKeysRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(Level.INFO, "Retrieving primary keys, using fast data dictionary retrieval");
        retrievePrimaryKeysFromDataDictionary();
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving primary keys");
        retrievePrimaryKeysFromMetadata(allTables);
        break;

      case data_dictionary_over_schemas:
        LOGGER.log(
            Level.INFO,
            "Retrieving primary keys, using fast data dictionary retrieval over schemas");
        retrievePrimaryKeysOverSchemas();
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving primary keys");
        break;
    }
  }

  private void createPrimaryKeyForTable(final MutableTable table, final MetadataResultSet results) {
    MutablePrimaryKey primaryKey;
    // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
    final String columnName = results.getString("COLUMN_NAME");
    final String primaryKeyName = results.getString("PK_NAME");
    final int keySequence = results.getShort("KEY_SEQ", (short) 1);
    LOGGER.log(
        Level.FINE,
        new StringFormat(
            "Retrieving primary column <%s.%s.%s>", table, primaryKeyName, columnName));

    primaryKey = table.getPrimaryKey();
    if (primaryKey == null) {
      primaryKey = MutablePrimaryKey.newPrimaryKey(table, primaryKeyName);
      table.setPrimaryKey(primaryKey);
    }

    // Register primary key information
    final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
    if (columnOptional.isPresent()) {
      final MutableColumn column = columnOptional.get();
      column.markAsPartOfPrimaryKey();
      final MutableTableConstraintColumn pkColumn =
          new MutableTableConstraintColumn(primaryKey, column);
      pkColumn.setKeyOrdinalPosition(keySequence);
      //
      primaryKey.addColumn(pkColumn);
    }

    primaryKey.addAttributes(results.getAttributes());
  }

  private void retrievePrimaryKeysFromDataDictionary() throws WrappedSQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(PRIMARY_KEYS)) {
      LOGGER.log(Level.FINE, "Extended primary keys SQL statement was not provided");
      return;
    }
    final Query pkSql = informationSchemaViews.getQuery(PRIMARY_KEYS);

    final String name = "primary keys from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(pkSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> optionalTable =
            lookupTable(catalogName, schemaName, tableName);
        if (!optionalTable.isPresent()) {
          continue;
        }
        final MutableTable table = optionalTable.get();
        createPrimaryKeyForTable(table, results);
        retrievalCounts.countIncluded();
      }
      retrievalCounts.log();
    } catch (final SQLException e) {
      throw new WrappedSQLException(
          String.format("Could not retrieve primary keys from SQL:%n%s", pkSql), e);
    }
  }

  private void retrievePrimaryKeysFromMetadata(final NamedObjectList<MutableTable> allTables)
      throws SQLException {
    final String name = "primary keys from metadata";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final MutableTable table : allTables) {
      if (isView(table)) {
        continue;
      }
      LOGGER.log(Level.INFO, new StringFormat("Retrieving %s for %s", name, table.key()));

      final Schema tableSchema = table.getSchema();
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection
                      .getMetaData()
                      .getPrimaryKeys(
                          tableSchema.getCatalogName(), tableSchema.getName(), table.getName()),
                  "DatabaseMetaData::getPrimaryKeys"); ) {
        while (results.next()) {
          retrievalCounts.count();
          createPrimaryKeyForTable(table, results);
          retrievalCounts.countIncluded();
        }
      } catch (final SQLException e) {
        logPossiblyUnsupportedSQLFeature(
            new StringFormat("Could not retrieve primary keys for table <%s>", table), e);
      }
    }
    retrievalCounts.log();
  }

  private void retrievePrimaryKeysOverSchemas() throws WrappedSQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(PRIMARY_KEYS)) {
      LOGGER.log(Level.FINE, "Extended primary keys SQL statement was not provided");
      return;
    }
    final Query pkSql = informationSchemaViews.getQuery(PRIMARY_KEYS);

    final Collection<Schema> schemas = catalog.getSchemas();
    final String name = "primary keys from data dictionary";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    for (final Schema schema : schemas) {
      if (catalog.getTables(schema).isEmpty()) {
        continue;
      }
      try (final Connection connection = getRetrieverConnection().getConnection(name);
          final SchemaSetter schemaSetter = new SchemaSetter(connection, schema);
          final Statement statement = connection.createStatement();
          final MetadataResultSet results =
              new MetadataResultSet(pkSql, statement, getLimitMap(schema)); ) {
        final String catalogName = schema.getCatalogName();
        while (results.next()) {
          retrievalCounts.count(schema.key());
          // final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
          final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
          final String tableName = results.getString("TABLE_NAME");

          final Optional<MutableTable> optionalTable =
              lookupTable(catalogName, schemaName, tableName);
          if (!optionalTable.isPresent()) {
            continue;
          }
          final MutableTable table = optionalTable.get();
          createPrimaryKeyForTable(table, results);
          retrievalCounts.countIncluded(schema.key());
        }
      } catch (final SQLException e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not retrieve primary keys for schema <%s>", schema));
      }
      retrievalCounts.log(schema.key());
    }
    retrievalCounts.log();
  }
}
