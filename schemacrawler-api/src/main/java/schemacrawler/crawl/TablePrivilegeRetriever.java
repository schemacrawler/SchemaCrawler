/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_COLUMN_PRIVILEGES;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_PRIVILEGES;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnPrivilegesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablePrivilegesRetrievalStrategy;

import schemacrawler.model.implementation.ColumnPointer;
import schemacrawler.model.implementation.MutableCatalog;
import schemacrawler.model.implementation.MutableColumn;
import schemacrawler.model.implementation.MutablePrivilege;
import schemacrawler.model.implementation.MutableTable;
import schemacrawler.model.implementation.TablePointer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;

/** A retriever uses database metadata to get the extended details about the database tables. */
final class TablePrivilegeRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TablePrivilegeRetriever.class.getName());

  TablePrivilegeRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  void retrieveTableColumnPrivileges() throws SQLException {
    switch (getRetrieverConnection().get(tableColumnPrivilegesRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(
            Level.INFO, "Retrieving column privileges, using fast data dictionary retrieval");
        retrieveTableColumnPrivilegesFromDataDictionary();
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving column privileges from metadata");
        retrieveTableColumnPrivilegesFromMetadata();
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving tables");
        break;
    }
  }

  void retrieveTablePrivileges() throws SQLException {
    switch (getRetrieverConnection().get(tablePrivilegesRetrievalStrategy)) {
      case data_dictionary_all:
        LOGGER.log(Level.INFO, "Retrieving table privileges, using fast data dictionary retrieval");
        retrieveTablePrivilegesFromDataDictionary();
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving table privileges from metadata");
        retrieveTablePrivilegesFromMetadata();
        break;

      default:
        LOGGER.log(Level.INFO, "Not retrieving tables");
        break;
    }
  }

  private void createPrivileges(final MetadataResultSet results, final boolean privilegesForColumn)
      throws SQLException {
    final RetrievalCounts retrievalCounts =
        new RetrievalCounts("%s privileges".formatted(privilegesForColumn ? "column" : "table"));
    while (results.next()) {
      retrievalCounts.count();
      final String catalogName = normalizeCatalogName(results.getString("TABLE_CAT"));
      final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEM"));
      final String tableName = results.getString("TABLE_NAME");
      final String columnName;
      if (privilegesForColumn) {
        columnName = results.getString("COLUMN_NAME");
      } else {
        columnName = null;
      }

      final Optional<MutableTable> tableOptional = lookupTable(catalogName, schemaName, tableName);
      if (tableOptional.isEmpty()) {
        continue;
      }

      final MutableTable table = tableOptional.get();
      final MutableColumn column;
      if (privilegesForColumn) {
        final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
        if (columnOptional.isEmpty()) {
          continue;
        }
        column = columnOptional.get();
      } else {
        column = null;
      }

      final String privilegeName = results.getString("PRIVILEGE");
      final String grantor = results.getString("GRANTOR");
      final String grantee = results.getString("GRANTEE");
      final boolean isGrantable = results.getBoolean("IS_GRANTABLE");

      final MutablePrivilege<?> privilege;
      if (privilegesForColumn) {
        final Optional<MutablePrivilege<Column>> privilegeOptional =
            column.lookupPrivilege(privilegeName);
        privilege =
            privilegeOptional.orElse(
                new MutablePrivilege<>(new ColumnPointer(column), privilegeName));
      } else {
        final Optional<MutablePrivilege<Table>> privilegeOptional =
            table.lookupPrivilege(privilegeName);
        privilege =
            privilegeOptional.orElse(
                new MutablePrivilege<>(new TablePointer(table), privilegeName));
      }
      privilege.withQuoting(getRetrieverConnection().getIdentifiers());

      privilege.addGrant(grantor, grantee, isGrantable);

      if (privilegesForColumn) {
        column.addPrivilege((MutablePrivilege<Column>) privilege);
      } else {
        table.addPrivilege((MutablePrivilege<Table>) privilege);
      }
      retrievalCounts.countIncluded();
    }
    retrievalCounts.log();
  }

  private void retrieveTableColumnPrivilegesFromDataDictionary() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(TABLE_COLUMN_PRIVILEGES)) {
      throw new ExecutionRuntimeException("No table column privileges SQL provided");
    }
    final Query tablePrivelegesSql = informationSchemaViews.getQuery(TABLE_COLUMN_PRIVILEGES);
    try (final Connection connection =
            getRetrieverConnection().getConnection("table column privileges from data dictionary");
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tablePrivelegesSql, statement, getLimitMap()); ) {
      createPrivileges(results, true);
    }
  }

  private void retrieveTableColumnPrivilegesFromMetadata() {
    try (final Connection connection =
            getRetrieverConnection().getConnection("table column privileges from metadata");
        final MetadataResultSet results =
            new MetadataResultSet(
                connection.getMetaData().getColumnPrivileges(null, null, null, null),
                "DatabaseMetaData::getColumnPrivileges"); ) {
      createPrivileges(results, true);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table column privileges:" + e.getMessage());
    }
  }

  private void retrieveTablePrivilegesFromDataDictionary() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(TABLE_PRIVILEGES)) {
      throw new ExecutionRuntimeException("No table privileges SQL provided");
    }
    final Query tablePrivelegesSql = informationSchemaViews.getQuery(TABLE_PRIVILEGES);
    try (final Connection connection =
            getRetrieverConnection().getConnection("table privileges from data dictionary");
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tablePrivelegesSql, statement, getLimitMap()); ) {
      createPrivileges(results, false);
    }
  }

  private void retrieveTablePrivilegesFromMetadata() {
    try (final Connection connection =
            getRetrieverConnection().getConnection("table privileges from metadata");
        final MetadataResultSet results =
            new MetadataResultSet(
                connection.getMetaData().getTablePrivileges(null, null, null),
                "DatabaseMetaData::getTablePrivileges"); ) {
      createPrivileges(results, false);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table privileges", e);
    }
  }
}
