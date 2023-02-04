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
import static schemacrawler.schemacrawler.InformationSchemaKey.PRIMARY_KEYS;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Schema;
import schemacrawler.schema.View;
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
        retrievePrimaryKeysFromDataDictionary(allTables);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving primary keys");
        retrievePrimaryKeysFromMetadata(allTables);
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
    final int keySequence = Integer.parseInt(results.getString("KEY_SEQ"));
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
  }

  private void retrievePrimaryKeysFromDataDictionary(final NamedObjectList<MutableTable> allTables)
      throws WrappedSQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(PRIMARY_KEYS)) {
      LOGGER.log(Level.FINE, "Extended primary keys SQL statement was not provided");
      return;
    }

    final Query pkSql = informationSchemaViews.getQuery(PRIMARY_KEYS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(pkSql, statement, getSchemaInclusionRule()); ) {
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
        createPrimaryKeyForTable(table, results);
      }
    } catch (final SQLException e) {
      throw new WrappedSQLException(
          String.format("Could not retrieve primary keys from SQL:%n%s", pkSql), e);
    }
  }

  private void retrievePrimaryKeysFromMetadata(final NamedObjectList<MutableTable> allTables)
      throws SQLException {
    for (final MutableTable table : allTables) {
      if (table instanceof View) {
        continue;
      }
      final Schema tableSchema = table.getSchema();
      try (final Connection connection = getRetrieverConnection().getConnection();
          final MetadataResultSet results =
              new MetadataResultSet(
                  connection
                      .getMetaData()
                      .getPrimaryKeys(
                          tableSchema.getCatalogName(), tableSchema.getName(), table.getName()),
                  "DatabaseMetaData::getPrimaryKeys"); ) {
        while (results.next()) {
          createPrimaryKeyForTable(table, results);
        }
      } catch (final SQLException e) {
        logPossiblyUnsupportedSQLFeature(
            new StringFormat("Could not retrieve primary keys for table <%s>", table), e);
      }
    }
  }
}
