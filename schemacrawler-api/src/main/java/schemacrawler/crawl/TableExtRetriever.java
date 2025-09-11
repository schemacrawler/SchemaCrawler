/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.ADDITIONAL_COLUMN_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaKey.ADDITIONAL_TABLE_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLES;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.plugin.EnumDataTypeInfo;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the extended details about the database tables. */
final class TableExtRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(TableExtRetriever.class.getName());

  TableExtRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves additional column attributes from the database.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveAdditionalColumnAttributes() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(ADDITIONAL_COLUMN_ATTRIBUTES)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional column attributes, since this was not requested");
      LOGGER.log(Level.FINE, "Additional column attributes SQL statement was not provided");
      return;
    }

    final String name = "columns with attibutes";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query columnAttributesSql = informationSchemaViews.getQuery(ADDITIONAL_COLUMN_ATTRIBUTES);

    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(columnAttributesSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        final String columnName = results.getString("COLUMN_NAME");
        LOGGER.log(Level.FINER, "Retrieving additional column attributes: " + columnName);

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        final Optional<MutableColumn> columnOptional = table.lookupColumn(columnName);
        if (!columnOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find column <%s.%s.%s.%s>",
                  catalogName, schemaName, tableName, columnName));
        } else {
          final MutableColumn column = columnOptional.get();
          column.addAttributes(results.getAttributes());
          retrievalCounts.countIncluded();
        }
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional column attributes", e);
    }
    retrievalCounts.log();
  }

  /** Retrieves additional column metadata. */
  void retrieveAdditionalColumnMetadata() {

    final String name = "columns with additional metadata";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    try (final Connection connection = getRetrieverConnection().getConnection(name); ) {
      final EnumDataTypeHelper enumDataTypeHelper =
          getRetrieverConnection().getEnumDataTypeHelper();

      final NamedObjectList<MutableTable> tables = catalog.getAllTables();
      for (final MutableTable table : tables) {
        retrievalCounts.count();
        final NamedObjectList<MutableColumn> columns = table.getAllColumns();
        for (final MutableColumn column : columns) {
          MutableColumnDataType columnDataType = (MutableColumnDataType) column.getColumnDataType();

          // Check for enumerated column data-types
          final EnumDataTypeInfo enumDataTypeInfo =
              enumDataTypeHelper.getEnumDataTypeInfo(column, columnDataType, connection);
          switch (enumDataTypeInfo.getType()) {
            case enumerated_column:
              // Create new column data-type with enumeration
              final MutableColumnDataType copiedColumnDataType =
                  new MutableColumnDataType(columnDataType);
              columnDataType = copiedColumnDataType; // overwrite with new column data-type
              columnDataType.setEnumValues(enumDataTypeInfo.getEnumValues());
              retrievalCounts.countIncluded();
              break;
            case enumerated_data_type:
              // Update column data-type with enumeration
              columnDataType.setEnumValues(enumDataTypeInfo.getEnumValues());
              retrievalCounts.countIncluded();
              break;
            default:
              break;
          }

          column.setColumnDataType(columnDataType);
        }
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional column metadata", e);
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves additional table attributes from the database.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveAdditionalTableAttributes() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(ADDITIONAL_TABLE_ATTRIBUTES)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional table attributes, since this was not requested");
      LOGGER.log(Level.FINE, "Additional table attributes SQL statement was not provided");
      return;
    }
    final Query tableAttributesSql = informationSchemaViews.getQuery(ADDITIONAL_TABLE_ATTRIBUTES);

    final String name = "tables with attributes";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);

    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableAttributesSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final boolean added = addAdditionalTableAttributes(results);
        retrievalCounts.countIfIncluded(added);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional table attributes", e);
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves table definitions from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveTableDefinitions() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(EXT_TABLES)) {
      LOGGER.log(Level.INFO, "Not retrieving table definitions, since this was not requested");
      LOGGER.log(Level.FINE, "Table definitions SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving table definitions");

    final String name = "table definitions";
    final RetrievalCounts retrievalCounts = new RetrievalCounts(name);
    final Query tableDefinitionsInformationSql = informationSchemaViews.getQuery(EXT_TABLES);
    try (final Connection connection = getRetrieverConnection().getConnection(name);
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableDefinitionsInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();

        LOGGER.log(Level.FINER, new StringFormat("Retrieving table information <%s>", tableName));
        final String definition = results.getString("TABLE_DEFINITION");

        table.setDefinition(definition);

        table.addAttributes(results.getAttributes());
        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table definitions", e);
    }
    retrievalCounts.log();
  }

  private boolean addAdditionalTableAttributes(final MetadataResultSet results)
      throws SQLException {
    final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
    final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
    final String tableName = results.getString("TABLE_NAME");
    LOGGER.log(Level.FINER, "Retrieving additional table attributes: " + tableName);

    final Optional<MutableTable> tableOptional = lookupTable(catalogName, schemaName, tableName);
    if (!tableOptional.isPresent()) {
      LOGGER.log(
          Level.FINE,
          new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
      return false;
    }

    final MutableTable table = tableOptional.get();
    table.addAttributes(results.getAttributes());

    return true;
  }
}
