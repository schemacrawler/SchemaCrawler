/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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
    final Query columnAttributesSql = informationSchemaViews.getQuery(ADDITIONAL_COLUMN_ATTRIBUTES);

    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(columnAttributesSql, statement, getLimitMap()); ) {
      int count = 0;
      int addedCount = 0;
      while (results.next()) {
        count = count + 1;
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
          addedCount = addedCount + 1;
        }
      }
      LOGGER.log(
          Level.INFO,
          new StringFormat("Processed %d/%d columns with attibutes", addedCount, count));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional column attributes", e);
    }
  }

  /** Retrieves additional column metadata. */
  void retrieveAdditionalColumnMetadata() {

    try (final Connection connection = getRetrieverConnection().getConnection(); ) {
      final EnumDataTypeHelper enumDataTypeHelper =
          getRetrieverConnection().getEnumDataTypeHelper();

      final NamedObjectList<MutableTable> tables = catalog.getAllTables();
      int count = 0;
      int addedCount = 0;
      for (final MutableTable table : tables) {
        count = count + 1;
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
              addedCount = addedCount + 1;
              break;
            case enumerated_data_type:
              // Update column data-type with enumeration
              columnDataType.setEnumValues(enumDataTypeInfo.getEnumValues());
              addedCount = addedCount + 1;
              break;
            default:
              break;
          }

          column.setColumnDataType(columnDataType);
        }
      }
      LOGGER.log(
          Level.INFO,
          new StringFormat("Processed %d/%d columns with additional metadata", addedCount, count));
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional column metadata", e);
    }
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

    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableAttributesSql, statement, getLimitMap()); ) {
      int count = 0;
      int addedCount = 0;
      while (results.next()) {
        count = count + 1;
        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");
        LOGGER.log(Level.FINER, "Retrieving additional table attributes: " + tableName);

        final Optional<MutableTable> tableOptional =
            lookupTable(catalogName, schemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        table.addAttributes(results.getAttributes());
        addedCount = addedCount + 1;
      }
      LOGGER.log(
          Level.INFO,
          new StringFormat("Processed %d/%d tables with attributes", addedCount, count));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve additional table attributes", e);
    }
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

    final Query tableDefinitionsInformationSql = informationSchemaViews.getQuery(EXT_TABLES);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(tableDefinitionsInformationSql, statement, getLimitMap()); ) {
      int count = 0;
      int addedCount = 0;
      while (results.next()) {
        count = count + 1;
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

        table.appendDefinition(definition);

        table.addAttributes(results.getAttributes());

        addedCount = addedCount + 1;
      }
      LOGGER.log(
          Level.INFO, new StringFormat("Processed %d/%d table definitions", addedCount, count));
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table definitions", e);
    }
  }
}
